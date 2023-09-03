package test.unit.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.AccountDAO;
import by.sakujj.dto.AccountRequest;
import by.sakujj.dto.AccountResponse;
import by.sakujj.dto.BankResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.AccountMapper;
import by.sakujj.model.Account;
import by.sakujj.model.Bank;
import by.sakujj.model.Currency;
import by.sakujj.services.AccountServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AccountServiceImplTests {
    private AutoCloseable mockitoClosable;

    @Mock
    private ConnectionPool connectionPool;

    @Mock
    private Connection connection;

    @Mock
    private AccountDAO accountDAO;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountServiceImpl;

    @BeforeEach
    void mockitoSetup() {
        mockitoClosable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void mockitoCleanup() throws Exception {
        mockitoClosable.close();
    }

    @Nested
    @DisplayName("findAll ()")
    public class findAll {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnAll() throws SQLException, DAOException {
            LocalDate localDate = LocalDate.now();
            List<AccountResponse> expectedAll = List.of(
                    AccountResponse.builder()
                            .id("xca")
                            .bankId(1L)
                            .currency(Currency.USD)
                            .dateWhenOpened(localDate)
                            .moneyAmount(new BigDecimal("210"))
                            .clientEmail("email1@gmail.com")
                            .build(),
                    AccountResponse.builder()
                            .id("XXXZ")
                            .bankId(2L)
                            .currency(Currency.USD)
                            .dateWhenOpened(localDate)
                            .moneyAmount(new BigDecimal("350"))
                            .clientEmail("email2@gmail.com")
                            .build()
            );
            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(accountDAO.findAll(connection))
                    .thenReturn(
                            expectedAll.stream()
                                    .map(r -> Account.builder()
                                            .id(r.getId())
                                            .build()
                                    ).toList()
                    );
            Mockito.when(accountMapper.toResponse(Mockito.any(), Mockito.any()))
                    .thenAnswer(invocation -> {
                        Account account = invocation.getArgument(0, Account.class);
                        for (AccountResponse accountResponse : expectedAll) {
                            if (account.getId().equals(accountResponse.getId())) {
                                return accountResponse;
                            }
                        }
                        return Account.builder().build();
                    });

            List<AccountResponse> actualAll = accountServiceImpl.findAll();
            assertThat(actualAll).containsExactlyInAnyOrderElementsOf(expectedAll);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> accountServiceImpl.findAll())
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowRuntimeException() throws SQLException, DAOException {
            String id = "ididid";
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.findAll(connection))
                    .thenReturn(List.of(Account.builder().build()));
            Mockito.when(accountMapper.toResponse(Mockito.any(), Mockito.any()))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> accountServiceImpl.findAll())
                    .isInstanceOf(RuntimeException.class);
        }


        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.findAll(connection))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> accountServiceImpl.findAll())
                    .isInstanceOf(DAOException.class);
        }
    }

    @Nested
    @DisplayName("findById (String)")
    public class findById {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnCorrectResponse() throws SQLException, DAOException {
            LocalDate localDate = LocalDate.now();
            String id = "xca";

            Optional<AccountResponse> expectedResponse = Optional.of(
                    AccountResponse.builder()
                            .id(id)
                            .bankId(1L)
                            .currency(Currency.USD)
                            .dateWhenOpened(localDate)
                            .moneyAmount(new BigDecimal("210"))
                            .clientEmail("email1@gmail.com")
                            .build()
            );

            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(accountDAO.findById(id, connection))
                    .thenReturn(Optional.of(Account.builder().build()));
            Mockito.when(accountMapper.toResponse(Mockito.any(), Mockito.any()))
                    .thenReturn(expectedResponse.get());

            Optional<AccountResponse> actual = accountServiceImpl.findById(id);
            assertThat(actual).isPresent();
            assertThat(actual).isEqualTo(expectedResponse);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowRuntimeException() throws SQLException, DAOException {
            String id = "ididid";
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.findById(id, connection))
                    .thenReturn(Optional.of(Account.builder().build()));
            Mockito.when(accountMapper.toResponse(Mockito.any(), Mockito.any()))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> accountServiceImpl.findById(id))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            String id = "xcxc";

            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> accountServiceImpl.findById(id))
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            String id = "xcxc";


            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.findById(id, connection))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> accountServiceImpl.findById(id))
                    .isInstanceOf(DAOException.class);
        }
    }

    @Nested
    @DisplayName("findByClientId (Long)")
    public class findByClientId {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnAllByClientId() throws SQLException, DAOException {
            Long clientId = 2L;
            LocalDate localDate = LocalDate.now();
            List<AccountResponse> expectedAll = List.of(
                    AccountResponse.builder()
                            .id("xca")
                            .bankId(1L)
                            .currency(Currency.USD)
                            .dateWhenOpened(localDate)
                            .moneyAmount(new BigDecimal("210"))
                            .clientEmail("email1@gmail.com")
                            .build(),
                    AccountResponse.builder()
                            .id("XXXZ")
                            .bankId(2L)
                            .currency(Currency.USD)
                            .dateWhenOpened(localDate)
                            .moneyAmount(new BigDecimal("350"))
                            .clientEmail("email2@gmail.com")
                            .build()
            );
            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(accountDAO.findByClientId(clientId, connection))
                    .thenReturn(
                            expectedAll.stream()
                                    .map(r -> Account.builder()
                                            .id(r.getId())
                                            .build()
                                    ).toList()
                    );
            Mockito.when(accountMapper.toResponse(Mockito.any(), Mockito.any()))
                    .thenAnswer(invocation -> {
                        Account account = invocation.getArgument(0, Account.class);
                        for (AccountResponse accountResponse : expectedAll) {
                            if (account.getId().equals(accountResponse.getId())) {
                                return accountResponse;
                            }
                        }
                        return Account.builder().build();
                    });

            List<AccountResponse> actualAll = accountServiceImpl.findAllByClientId(clientId);
            assertThat(actualAll).containsExactlyInAnyOrderElementsOf(expectedAll);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowRuntimeException() throws SQLException, DAOException {
            Long clientId = 2L;
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.findByClientId(clientId, connection))
                    .thenReturn(List.of(Account.builder().build()));
            Mockito.when(accountMapper.toResponse(Mockito.any(), Mockito.any()))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> accountServiceImpl.findAllByClientId(clientId))
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            Long clientId = 2L;
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> accountServiceImpl.findAllByClientId(clientId))
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            Long clientId = 2L;
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.findByClientId(clientId, connection))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> accountServiceImpl.findAllByClientId(clientId))
                    .isInstanceOf(DAOException.class);
        }
    }

    @Nested
    @DisplayName("updateMoneyAmountById (BigDecimal, String)")
    public class updateMoneyAmountById {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldUpdateMoneyAmountById() throws SQLException, DAOException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenReturn(true);

            boolean actual = accountServiceImpl.updateMoneyAmountById(
                    new BigDecimal("324.43"), "ididid");

            assertThat(actual).isTrue();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldCatchDAOException() throws DAOException, SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountById(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenThrow(new DAOException());

            boolean actual = accountServiceImpl.updateMoneyAmountById(
                    new BigDecimal("324.43"), "ididid");

            assertThat(actual).isFalse();

        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> accountServiceImpl.updateMoneyAmountById(
                    new BigDecimal("324.43"), "ididid"
            )).isInstanceOf(SQLException.class);
        }
    }

    @Nested
    @DisplayName("updateMoneyAmountByPercentage (BigDecimal, String)")
    public class updateMoneyAmountByPercentage {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldUpdateMoneyAmountByPercentage() throws SQLException, DAOException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountByPercentage(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenReturn(true);

            boolean actual = accountServiceImpl.updateMoneyAmountByPercentage(
                    new BigDecimal("324.43"), "ididid");

            assertThat(actual).isTrue();
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws SQLException, DAOException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.updateMoneyAmountByPercentage(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenThrow(new DAOException());

            assertThatThrownBy(
                    () ->
                            accountServiceImpl.updateMoneyAmountByPercentage(new BigDecimal("100"), "ididid")

            );
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> accountServiceImpl.updateMoneyAmountByPercentage(
                    new BigDecimal("324.43"), "ididid"
            )).isInstanceOf(SQLException.class);
        }
    }

    @Nested
    @DisplayName("save (AccountRequest)")
    public class save {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldSaveAccount() throws SQLException, DAOException {
            String expectedId = "ididid";
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountDAO.save(Mockito.any(), Mockito.any()))
                    .thenReturn(expectedId);
            Mockito.when(accountMapper.fromRequest(Mockito.any(), Mockito.any()))
                    .thenReturn(Account.builder().build());

            String actualId = accountServiceImpl.save(AccountRequest.builder().build());
            assertThat(actualId).isEqualTo(expectedId);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> accountServiceImpl.save(AccountRequest.builder().build()))
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOExceptionBecauseOfMapper() throws SQLException, DAOException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountMapper.fromRequest(Mockito.any(), Mockito.any()))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> accountServiceImpl.save(AccountRequest.builder().build()))
                    .isInstanceOf(DAOException.class);
        }


        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOExceptionBecauseOfDao() throws DAOException, SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(accountMapper.fromRequest(Mockito.any(), Mockito.any()))
                    .thenReturn(Account.builder().build());
            Mockito.when(accountDAO.save(Mockito.any(), Mockito.any()))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> accountServiceImpl.save(AccountRequest.builder().build()))
                    .isInstanceOf(DAOException.class);
        }
    }
}
