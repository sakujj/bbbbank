package test.unit.mappers;

import by.sakujj.dto.AccountRequest;
import by.sakujj.dto.AccountResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.AccountMapper;
import by.sakujj.model.Account;
import by.sakujj.model.Currency;
import by.sakujj.util.AccountIdGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.integration.connection.AbstractConnectionRelatedTests;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class AccountMapperTests extends AbstractConnectionRelatedTests {

    private static final AccountMapper mapper = AccountMapper.getInstance();

    @ParameterizedTest
    @MethodSource
    void toAccount(AccountRequest request, Account expected) throws DAOException {
        Account actual = mapper.fromRequest(request, getConnection());

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> toAccount() {
        Long clientId = 20L;
        String clientEmail = "email19@gmail.com";
        Long bankId = 12345678910L;
        Currency currency = Currency.USD;
        return Stream.of(arguments(
                AccountRequest.builder()
                        .bankId(bankId.toString())
                        .clientEmail(clientEmail)
                        .currency(currency.toString())
                        .build(),
                Account.builder()
                        .id(AccountIdGenerator
                                .generateAccountId(bankId, clientId))
                        .dateWhenOpened(null)
                        .clientId(clientId)
                        .moneyAmount(new BigDecimal("0.00"))
                        .bankId(bankId)
                        .currency(currency)
                        .build()
        ));
    }

    @ParameterizedTest
    @MethodSource
    void toAccountResponse(Account account, AccountResponse expected) throws DAOException {
        AccountResponse actual = mapper.toResponse(account, getConnection());

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> toAccountResponse() {
        String id = "123412341234123412341234XX39";
        Long clientId = 20L;
        String clientEmail = "email19@gmail.com";
        Long bankId = 12345678910L;
        BigDecimal moneyAmount = new BigDecimal("2000.39");
        Currency currency = Currency.USD;
        LocalDate dateWhenOpened = LocalDate.parse("2020-04-19");
        return Stream.of(arguments(
                Account.builder()
                        .id(id)
                        .dateWhenOpened(dateWhenOpened)
                        .clientId(clientId)
                        .moneyAmount(moneyAmount)
                        .bankId(bankId)
                        .currency(currency)
                        .build(),
                AccountResponse.builder()
                        .id(id)
                        .dateWhenOpened(dateWhenOpened.toString())
                        .moneyAmount(moneyAmount.toString())
                        .bankId(bankId.toString())
                        .clientEmail(clientEmail)
                        .currency(currency.toString())
                        .build()
        ));
    }
}
