package test.unit.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.AccountDAO;
import by.sakujj.dao.MonetaryTransactionDAO;
import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.MonetaryTransactionMapper;
import by.sakujj.model.MonetaryTransaction;
import by.sakujj.services.MonetaryTransactionServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class MonetaryTransactionServiceTests {
    private AutoCloseable mockitoClosable;

    @Mock
    private MonetaryTransactionDAO monetaryTransactionDAO;
    @Mock
    private MonetaryTransactionMapper monetaryTransactionMapper;
    @Mock
    private AccountDAO accountDAO;
    @Mock
    private ConnectionPool connectionPool;
    @Mock
    private Connection connection;

    private MonetaryTransactionServiceImpl monetaryTransactionServiceImpl;

    @BeforeEach
    void mockitoSetup() {
        mockitoClosable = MockitoAnnotations.openMocks(this);
        monetaryTransactionServiceImpl = Mockito.spy(new MonetaryTransactionServiceImpl(
                monetaryTransactionDAO,
                monetaryTransactionMapper,
                accountDAO,
                connectionPool
        ));
    }

    @AfterEach
    void mockitoCleanup() throws Exception {
        mockitoClosable.close();
    }

    @Nested
    @DisplayName("createDepositTransaction (MonetaryTransaction)")
    public class createDepositTransaction {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnTransaction() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.eq(connection)))
                    .thenReturn(true);
            Mockito.doReturn(Optional.of(MonetaryTransactionResponse.builder().build())).when(monetaryTransactionServiceImpl)
                    .getMonetaryTransactionResponse(Mockito.any(), Mockito.eq(connection));

            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createDepositTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .receiverAccountId(Optional.of("idid"))
                            .build());
            assertThat(actual).isPresent();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnEmpty() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.eq(connection)))
                    .thenReturn(false);

            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createDepositTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .receiverAccountId(Optional.of("idid"))
                            .build());
            assertThat(actual).isEmpty();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(SQLException.class);

            assertThatThrownBy(() -> monetaryTransactionServiceImpl
                    .createDepositTransaction(MonetaryTransactionRequest.builder()
                            .build())).isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.eq(connection)))
                    .thenThrow(DAOException.class);

            assertThatThrownBy(() -> monetaryTransactionServiceImpl
                    .createDepositTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .receiverAccountId(Optional.of("idid"))
                            .build())).isInstanceOf(DAOException.class);

        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLExceptionOnClose() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(true);
            Mockito.doThrow(new SQLException()).when(connection).close();

            assertThatThrownBy(() -> monetaryTransactionServiceImpl.createDepositTransaction(
                    MonetaryTransactionRequest.builder().build()
            ));
        }

    }

    @Nested
    @DisplayName("createTransferTransaction (MonetaryTransaction)")
    public class createTransferTransaction {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnEmpty() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());


            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createTransferTransaction(MonetaryTransactionRequest.builder()
                            .build());
            assertThat(actual).isEmpty();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnEmptyBecauseOfAutocommitFalse() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.doThrow(new SQLException()).when(connection).setAutoCommit(false);


            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createTransferTransaction(MonetaryTransactionRequest.builder()
                            .build());
            assertThat(actual).isEmpty();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnEmptyBecauseOfAutocommitTrue() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(true);
            Mockito.doThrow(new SQLException())
                    .doNothing()
                    .when(connection).setAutoCommit(true);


            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createTransferTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .receiverAccountId(Optional.of("recv_id"))
                            .senderAccountId(Optional.of("sndr_id"))
                            .build());
            assertThat(actual).isEmpty();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnEmptyBecauseOfCommit() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(true);
            Mockito.doThrow(new SQLException())
                    .when(connection).commit();


            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createTransferTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .receiverAccountId(Optional.of("recv_id"))
                            .senderAccountId(Optional.of("sndr_id"))
                            .build());
            assertThat(actual).isEmpty();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnPresent() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(true);
            Mockito.doReturn(Optional.of(MonetaryTransactionResponse.builder().build()))
                    .when(monetaryTransactionServiceImpl).getMonetaryTransactionResponse(Mockito.any(), Mockito.same(connection));


            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createTransferTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .receiverAccountId(Optional.of("recv_id"))
                            .senderAccountId(Optional.of("sndr_id"))
                            .build());
            assertThat(actual).isPresent();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnEmptyNotUpdatedSender() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(false);

            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createTransferTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .receiverAccountId(Optional.of("recv_id"))
                            .senderAccountId(Optional.of("sndr_id"))
                            .build());
            assertThat(actual).isEmpty();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnEmptyNotUpdatedReceiver() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(true).thenReturn(false);

            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createTransferTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .receiverAccountId(Optional.of("recv_id"))
                            .senderAccountId(Optional.of("sndr_id"))
                            .build());
            assertThat(actual).isEmpty();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLExceptionOnSecondSetAutocommit() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(true);
            Mockito.doThrow(new SQLException()).when(connection).setAutoCommit(true);

            assertThatThrownBy(() -> monetaryTransactionServiceImpl.createTransferTransaction(
                    MonetaryTransactionRequest.builder().build()
            ));
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLExceptionOnRollback() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(true);
            Mockito.doThrow(new SQLException()).when(connection).rollback();

            assertThatThrownBy(() -> monetaryTransactionServiceImpl.createTransferTransaction(
                    MonetaryTransactionRequest.builder().build()
            ));
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLExceptionOnClose() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(true);
            Mockito.doThrow(new SQLException()).when(connection).close();

            assertThatThrownBy(() -> monetaryTransactionServiceImpl.createTransferTransaction(
                    MonetaryTransactionRequest.builder().build()
            ));
        }
    }

    @Nested
    @DisplayName("createWithdrawalTransaction (MonetaryTransaction)")
    public class createWithdrawalTransaction {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnTransaction() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.eq(connection)))
                    .thenReturn(true);
            Mockito.doReturn(Optional.of(MonetaryTransactionResponse.builder().build())).when(monetaryTransactionServiceImpl)
                    .getMonetaryTransactionResponse(Mockito.any(), Mockito.eq(connection));

            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createWithdrawalTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .senderAccountId(Optional.of("idid"))
                            .build());
            assertThat(actual).isPresent();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnEmpty() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.eq(connection)))
                    .thenReturn(false);

            Optional<MonetaryTransactionResponse> actual = monetaryTransactionServiceImpl
                    .createWithdrawalTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .senderAccountId(Optional.of("idid"))
                            .build());
            assertThat(actual).isEmpty();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(SQLException.class);

            assertThatThrownBy(() -> monetaryTransactionServiceImpl
                    .createWithdrawalTransaction(MonetaryTransactionRequest.builder()
                            .build())).isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.eq(connection)))
                    .thenThrow(DAOException.class);

            assertThatThrownBy(() -> monetaryTransactionServiceImpl
                    .createWithdrawalTransaction(MonetaryTransactionRequest.builder()
                            .moneyAmount("20.00")
                            .senderAccountId(Optional.of("idid"))
                            .build())).isInstanceOf(DAOException.class);

        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLExceptionOnClose() throws Exception {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.same(connection)))
                    .thenReturn(true);
            Mockito.doThrow(new SQLException()).when(connection).close();

            assertThatThrownBy(() -> monetaryTransactionServiceImpl.createWithdrawalTransaction(
                    MonetaryTransactionRequest.builder().build()
            ));
        }
    }

    @Test
    void shouldNotThrowExceptionsOnClose() {
        assertThatNoException().isThrownBy(
                () -> monetaryTransactionServiceImpl.close()
        );
    }
    @Nested
    @DisplayName("getMonetaryTransactionResponse (MonetaryTransaction, Connection)")
    public class getMonetaryTransactionResponse {
        private Method method;

        @BeforeEach
        @SneakyThrows
        void getTestedMethod() {
            method = monetaryTransactionServiceImpl.getClass().getDeclaredMethod(
                    "getMonetaryTransactionResponse",
                    MonetaryTransactionRequest.class,
                    Connection.class
            );
            method.setAccessible(true);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnMonetaryTransactionResponse() throws DAOException, InvocationTargetException, IllegalAccessException {
            Optional<MonetaryTransactionResponse> expected = Optional.of(
                    MonetaryTransactionResponse.builder().build()
            );
            Mockito.when(monetaryTransactionMapper.fromRequest(Mockito.any()))
                    .thenReturn(MonetaryTransaction.builder().build());
            Mockito.when(monetaryTransactionDAO.save(Mockito.any(), Mockito.any()))
                    .thenReturn(1L);
            Mockito.when(monetaryTransactionMapper.toResponse(Mockito.any(), Mockito.any()))
                    .thenReturn(expected.get());


            @SuppressWarnings("unchecked")
            Optional<MonetaryTransactionResponse> actual = (Optional<MonetaryTransactionResponse>) method
                    .invoke(monetaryTransactionServiceImpl,
                            MonetaryTransactionRequest.builder().build(),
                            connection);

            assertThat(actual).isEqualTo(expected);
        }
    }
}


