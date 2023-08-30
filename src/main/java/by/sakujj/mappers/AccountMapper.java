package by.sakujj.mappers;

import by.sakujj.dao.AccountDAO;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.AccountRequest;
import by.sakujj.dto.AccountResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.model.Account;
import by.sakujj.model.Client;
import by.sakujj.model.Currency;
import by.sakujj.util.AccountIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;

@Mapper
@Setter
public abstract class AccountMapper {
    private ClientDAO clientDAO;
    private AccountDAO accountDAO;

    public Account fromRequest(AccountRequest accountRequest, Connection connection) throws DAOException {
        Client client = clientDAO.findByEmail(accountRequest.getClientEmail(), connection).get();
        Long clientId = client.getId();
        Currency currency = Currency.valueOf(accountRequest.getCurrency());
        Long bankId = Long.valueOf(accountRequest.getBankId());
        BigDecimal moneyAmount = new BigDecimal("0.00");
        int clientAccountNumber = accountDAO.findByClientId(clientId, connection).size();

        return Account.builder()
                .id(AccountIdGenerator
                        .generateAccountId(clientAccountNumber, bankId, clientId))
                .dateWhenOpened(LocalDate.now())
                .moneyAmount(moneyAmount)
                .bankId(bankId)
                .clientId(clientId)
                .currency(currency)
                .build();
    }


    public AccountResponse toResponse(Account account, Connection connection) throws DAOException {
        Client client = clientDAO.findById(account.getClientId(), connection).get();
        String clientEmail = client
                .getEmail();
        Currency currency = account.getCurrency();
        Long bankId = account.getBankId();
        BigDecimal moneyAmount = account.getMoneyAmount();
        LocalDate dateWhenOpened = account.getDateWhenOpened();
        String id = account.getId();

        return AccountResponse.builder()
                .id(id)
                .clientEmail(clientEmail)
                .moneyAmount(moneyAmount)
                .dateWhenOpened(dateWhenOpened)
                .bankId(bankId)
                .currency(currency)
                .build();
    }
}
