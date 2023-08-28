package test.unit.mappers;

import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.AccountRequest;
import by.sakujj.dto.AccountResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.AccountMapper;
import by.sakujj.model.Account;
import by.sakujj.model.Client;
import by.sakujj.model.Currency;
import by.sakujj.util.AccountIdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class AccountMapperTests {

    private AutoCloseable mockitoClosable;

    @Mock
    private ClientDAO clientDAO;

    @Mock
    private Connection connection;

    @InjectMocks
    private AccountMapper mapper = AccountMapper.getInstance();

    @BeforeEach
    void mockitoSetup() {
        mockitoClosable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void mockitoCleanup() throws Exception {
          mockitoClosable.close();
    }

    @Execution(ExecutionMode.SAME_THREAD)
    @ParameterizedTest
    @MethodSource
    void toAccount(AccountRequest request, Account expected) throws DAOException, NoSuchFieldException {
        Long expectedId = expected.getClientId();
        Mockito.when(clientDAO.findByEmail(Mockito.any(), Mockito.any())).thenReturn(
                Optional.of(Client.builder()
                        .id(expectedId)
                        .build()));

        Account actual = mapper.fromRequest(request, connection);

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

    @Execution(ExecutionMode.SAME_THREAD)
    @ParameterizedTest
    @MethodSource
    void toAccountResponse(Account account, AccountResponse expected) throws DAOException {
        String expectedEmail = expected.getClientEmail();
        Mockito.when(clientDAO.findById(Mockito.any(), Mockito.any())).thenReturn(
                Optional.of(Client.builder()
                        .email(expectedEmail)
                        .build()));

        AccountResponse actual = mapper.toResponse(account, connection);

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
