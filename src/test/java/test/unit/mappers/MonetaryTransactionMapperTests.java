package test.unit.mappers;

import by.sakujj.context.ApplicationContext;
import by.sakujj.dao.AccountDAO;
import by.sakujj.dao.BankDAO;
import by.sakujj.dto.AccountResponse;
import by.sakujj.dto.BankResponse;
import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.MonetaryTransactionMapper;
import by.sakujj.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

public class MonetaryTransactionMapperTests {
    private static final ApplicationContext context = ApplicationContext.getTestInstance();

    private AutoCloseable mockitoClosable;

    @Mock
    private Connection connection;

    @Mock
    private BankDAO bankDAO;

    @Mock
    private AccountDAO accountDAO;

    @InjectMocks
    private MonetaryTransactionMapper mapper = context.getByClass(MonetaryTransactionMapper.class);

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
    void toMonetaryTransaction(MonetaryTransactionRequest request, MonetaryTransaction expected) {
        MonetaryTransaction actual = mapper.fromRequest(request);

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> toMonetaryTransaction() {
        String senderAccountId = "XXID232";
        String receiverAccountId = "YYID232";
        BigDecimal moneyAmount = new BigDecimal("444.45");
        MonetaryTransactionType type = MonetaryTransactionType.TRANSFER;

        return Stream.of(
                arguments(
                        MonetaryTransactionRequest.builder()
                                .senderAccountId(senderAccountId)
                                .receiverAccountId(receiverAccountId)
                                .moneyAmount(moneyAmount.toString())
                                .type(type.toString())
                                .build(),
                        MonetaryTransaction.builder()
                                .id(null)
                                .timeWhenCommitted(null)
                                .senderAccountId(senderAccountId)
                                .receiverAccountId(receiverAccountId)
                                .moneyAmount(moneyAmount)
                                .type(type)
                                .build()
                )
        );

    }

    @Execution(ExecutionMode.SAME_THREAD)
    @ParameterizedTest
    @MethodSource
    void toMonetaryTransactionResponse(MonetaryTransaction transaction, MonetaryTransactionResponse expected) throws DAOException {

        Mockito.when(accountDAO.findById(Mockito.anyString(), Mockito.any())).thenAnswer(invocation -> {
            String arg = invocation.getArgument(0, String.class);
            String senderAccountId = transaction.getSenderAccountId();
            String receiverAccountId = transaction.getReceiverAccountId();
            if (arg.equals(senderAccountId)) {
                return Optional.of(Account.builder()
                        .currency(Currency.USD)
                        .bankId(666L)
                        .build());
            } else if (arg.equals(receiverAccountId)) {
                return Optional.of(Account.builder()
                        .currency(Currency.USD)
                        .bankId(999L)
                        .build());
            }
            return Optional.empty();
        });

        Mockito.when(bankDAO.findById(Mockito.anyLong(), Mockito.any())).thenAnswer(invocation -> {
            Long arg = invocation.getArgument(0, Long.class);
            Long senderBankId = 666L;
            Long receiverBankId = 999L;
            if (arg.equals(senderBankId)) {
                return Optional.of(Bank.builder()
                        .name("SENDER BANK")
                        .build());
            } else if (arg.equals(receiverBankId)) {
                return Optional.of(Bank.builder()
                        .name("RECEIVER BANK")
                        .build());
            }
            return Optional.empty();
        });
        MonetaryTransactionResponse actual = mapper.toResponse(transaction, connection);

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> toMonetaryTransactionResponse() {
        Long id = 44L;
        LocalDateTime timeWhenCommitted = LocalDateTime.now();
        String senderAccountId = "XXID232";
        String receiverAccountId = "YYID232";
        BigDecimal moneyAmount = new BigDecimal("444.45");
        MonetaryTransactionType type = MonetaryTransactionType.TRANSFER;
        return Stream.of(arguments(
                MonetaryTransaction.builder()
                        .id(id)
                        .timeWhenCommitted(timeWhenCommitted)
                        .senderAccountId(senderAccountId)
                        .receiverAccountId(receiverAccountId)
                        .moneyAmount(moneyAmount)
                        .type(type)
                        .build(),
                MonetaryTransactionResponse.builder()
                        .id(id)
                        .timeWhenCommitted(timeWhenCommitted)
                        .senderAccountId(senderAccountId)
                        .receiverAccountId(receiverAccountId)
                        .moneyAmount(moneyAmount)
                        .bankReceiverName("RECEIVER BANK")
                        .bankSenderName("SENDER BANK")
                        .currency(Currency.USD)
                        .type(type)
                        .build()
        ));
    }

}
