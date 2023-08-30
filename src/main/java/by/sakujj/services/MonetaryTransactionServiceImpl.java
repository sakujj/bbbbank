package by.sakujj.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.AccountDAO;
import by.sakujj.dao.MonetaryTransactionDAO;
import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.MonetaryTransactionMapper;
import by.sakujj.model.MonetaryTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MonetaryTransactionServiceImpl implements MonetaryTransactionService {

    private final MonetaryTransactionDAO monetaryTransactionDAO;
    private final MonetaryTransactionMapper monetaryTransactionMapper;
    private final AccountDAO accountDAO;
    private final ConnectionPool connectionPool;

    @SneakyThrows
    public Optional<MonetaryTransactionResponse> createDepositTransaction(MonetaryTransactionRequest request) {
        try (Connection connection = connectionPool.getConnection()) {
            BigDecimal moneyAmount = new BigDecimal(request.getMoneyAmount());
            String accountId = request.getReceiverAccountId();
            boolean isUpdated = accountDAO.updateMoneyAmountById(moneyAmount, accountId, connection);
            if (!isUpdated) {
                return Optional.empty();
            }

            return getMonetaryTransactionResponse(request, connection);
        }
    }

    @SneakyThrows
    public Optional<MonetaryTransactionResponse> createWithdrawalTransaction(MonetaryTransactionRequest request) {
        try (Connection connection = connectionPool.getConnection()) {
            BigDecimal moneyAmount = new BigDecimal(request.getMoneyAmount());
            String accountId = request.getSenderAccountId();
            boolean isUpdated = accountDAO.updateMoneyAmountById(moneyAmount.negate(), accountId, connection);
            if (!isUpdated) {
                return Optional.empty();
            }

            return getMonetaryTransactionResponse(request, connection);
        }
    }

    private Optional<MonetaryTransactionResponse> getMonetaryTransactionResponse(MonetaryTransactionRequest request, Connection connection) throws DAOException {
        LocalDateTime timeWhenCommitted = LocalDateTime.now();

        MonetaryTransactionRequest.builder()
                .senderAccountId(request.getSenderAccountId())
                .receiverAccountId(request.getReceiverAccountId())
                .moneyAmount(request.getMoneyAmount())
                .type(request.getType())
                .build();
        MonetaryTransaction monetaryTransaction = monetaryTransactionMapper.fromRequest(request);
        monetaryTransaction.setTimeWhenCommitted(timeWhenCommitted);

        Long id = monetaryTransactionDAO.save(monetaryTransaction, connection);
        monetaryTransaction.setId(id);

        return Optional.of(monetaryTransactionMapper.toResponse(monetaryTransaction, connection));
    }

    @SneakyThrows
    public Optional<MonetaryTransactionResponse> createTransferTransaction(MonetaryTransactionRequest request) {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            connection.setAutoCommit(false);

            BigDecimal moneyAmount = new BigDecimal(request.getMoneyAmount());
            String senderId = request.getSenderAccountId();
            boolean isUpdated = accountDAO.updateMoneyAmountById(moneyAmount.negate(), senderId, connection);
            if (!isUpdated) {
                return Optional.empty();
            }

            String receiverId = request.getReceiverAccountId();
            isUpdated = accountDAO.updateMoneyAmountById(moneyAmount, receiverId, connection);
            if (!isUpdated) {
                return Optional.empty();
            }

            connection.commit();
            connection.setAutoCommit(true);

            return getMonetaryTransactionResponse(request, connection);

        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
            return Optional.empty();

        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void close() throws Exception {

    }
}

