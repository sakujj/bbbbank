package by.sakujj.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.AccountDAO;
import by.sakujj.dto.AccountRequest;
import by.sakujj.dto.AccountResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.AccountMapper;
import by.sakujj.model.Account;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountServiceImpl implements AccountService {
    private final ConnectionPool connectionPool;
    private final AccountMapper accountMapper;
    private final AccountDAO accountDAO;

    @SneakyThrows
    public boolean updateMoneyAmountById(BigDecimal moneyAmount, String id) {
        try (Connection connection = connectionPool.getConnection()) {
            accountDAO.updateMoneyAmountById(moneyAmount, id, connection);
            return true;
        } catch (DAOException e) {
            return false;
        }
    }

    @SneakyThrows
    public boolean updateMoneyAmountByPercentage(BigDecimal percentage, String id) {
        try (Connection connection = connectionPool.getConnection()) {
            accountDAO.updateMoneyAmountByPercentage(percentage, id, connection);
            return true;
        }
    }

    @SneakyThrows
    public Optional<AccountResponse> findById(String id) {
        try (Connection connection = connectionPool.getConnection()) {
            Optional<Account> foundAccount = accountDAO.findById(id, connection);

            return foundAccount.map(a -> {
                try {
                    return accountMapper.toResponse(a, connection);
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @SneakyThrows
    public List<AccountResponse> findAll() {
        try (Connection connection = connectionPool.getConnection()) {
            List<AccountResponse> accountResponses = accountDAO.findAll(connection)
                    .stream()
                    .map(a -> {
                        try {
                            return accountMapper.toResponse(a, connection);
                        } catch (DAOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            return accountResponses;
        }
    }

    @SneakyThrows
    public List<AccountResponse> findAllByClientId(Long clientId) {
        try (Connection connection = connectionPool.getConnection()) {
            List<AccountResponse> accountResponses = accountDAO.findByClientId(clientId, connection)
                    .stream()
                    .map(a -> {
                        try {
                            return accountMapper.toResponse(a, connection);
                        } catch (DAOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
            return accountResponses;
        }
    }

    @SneakyThrows
    public String save(AccountRequest request) {
        try (Connection connection = connectionPool.getConnection()) {
            Account account = accountMapper.fromRequest(request, connection);

            return accountDAO.save(account, connection);
        }
    }
}
