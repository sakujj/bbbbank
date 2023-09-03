package by.sakujj.mappers;

import by.sakujj.dao.AccountDAO;
import by.sakujj.dao.BankDAO;
import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.model.*;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;

@Mapper
@Setter
public abstract class MonetaryTransactionMapper {

    private BankDAO bankDAO;
    private AccountDAO accountDAO;

    String map(Optional<String> value) {
        return value.orElse(null);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target="timeWhenCommitted", ignore = true)
    public abstract MonetaryTransaction fromRequest(MonetaryTransactionRequest request);

    public MonetaryTransactionResponse toResponse(MonetaryTransaction monetaryTransaction, Connection connection) throws DAOException {
        String senderAccountId = monetaryTransaction.getSenderAccountId();
        String receiverAccountId = monetaryTransaction.getReceiverAccountId();
        Optional<Account> possibleSenderAccount = accountDAO.findById(senderAccountId, connection);
        Optional<Account> possibleReceiverAccount = accountDAO.findById(receiverAccountId, connection);
        String senderBankName = null;
        String receiverBankName = null;
        Currency currency = null;
        if (possibleSenderAccount.isPresent()) {
            Account senderAccount = possibleSenderAccount.get();
            senderBankName = bankDAO.findById(senderAccount.getBankId(), connection)
                    .get().getName();
            currency = senderAccount.getCurrency();
        }

        if (possibleReceiverAccount.isPresent()) {
            Account receiverAccount = possibleReceiverAccount.get();
            receiverBankName = bankDAO.findById(receiverAccount.getBankId(), connection)
                    .get().getName();
            currency = receiverAccount.getCurrency();
        }

        return MonetaryTransactionResponse.builder()
                .id(monetaryTransaction.getId())
                .bankSenderName(Optional.ofNullable(senderBankName))
                .bankReceiverName(Optional.ofNullable(receiverBankName))
                .moneyAmount(monetaryTransaction.getMoneyAmount())
                .currency(currency)
                .type(monetaryTransaction.getType())
                .timeWhenCommitted(monetaryTransaction.getTimeWhenCommitted())
                .senderAccountId(Optional.ofNullable(senderAccountId))
                .receiverAccountId(Optional.ofNullable(receiverAccountId))
                .build();
    }
}
