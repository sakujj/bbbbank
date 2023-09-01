package test.unit.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.BankDAO;
import by.sakujj.dto.BankResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.BankMapper;
import by.sakujj.model.Bank;
import by.sakujj.services.BankServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

public class BankServiceImplTests {
    private AutoCloseable mockitoClosable;

    @Mock
    private ConnectionPool connectionPool;

    @Mock
    private Connection connection;

    @Mock
    private BankDAO bankDAO;

    @Mock
    private BankMapper bankMapper;

    @InjectMocks
    private BankServiceImpl bankServiceImpl;

    @BeforeEach
    void mockitoSetup() {
        mockitoClosable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void mockitoCleanup() throws Exception {
        mockitoClosable.close();
    }

    @Nested
    @DisplayName("findById (Long)")
    public class findById {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnCorrectResponse() throws SQLException, DAOException {
            Long id = 55L;
            String name = "bbank";
            Optional<BankResponse> expectedResponse = Optional.of(BankResponse.builder()
                    .id(id)
                    .name(name)
                    .build());
            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(bankDAO.findById(id, connection))
                    .thenReturn(Optional.of(Bank.builder()
                            .id(id)
                            .name(name)
                            .build())
                    );
            Mockito.when(bankMapper.toResponse(Mockito.any()))
                    .thenReturn(expectedResponse.get());

            Optional<BankResponse> actual = bankServiceImpl.findById(id);
            assertThat(actual).isPresent();
            assertThat(actual).isEqualTo(expectedResponse);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException{
            Long id = 55L;

            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> bankServiceImpl.findById(id))
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            Long id = 55L;

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(bankDAO.findById(Mockito.anyLong(), Mockito.any()))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> bankServiceImpl.findById(id))
                    .isInstanceOf(DAOException.class);
        }
    }

    @Nested
    @DisplayName("findByName (String)")
    public class findByName {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnCorrectResponse() throws SQLException, DAOException {
            Long id = 55L;
            String name = "bbank";
            Optional<BankResponse> expectedResponse = Optional.of(BankResponse.builder()
                    .id(id)
                    .name(name)
                    .build());
            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(bankDAO.findByName(name, connection))
                    .thenReturn(Optional.of(Bank.builder()
                            .id(id)
                            .name(name)
                            .build())
                    );
            Mockito.when(bankMapper.toResponse(Mockito.any())).thenReturn(
                    expectedResponse.get()
            );

            Optional<BankResponse> actual = bankServiceImpl.findByName(name);
            assertThat(actual).isPresent();
            assertThat(actual).isEqualTo(expectedResponse);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException, DAOException {
            String name = "bbank";

            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> bankServiceImpl.findByName(name))
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            String name = "bbank";

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(bankDAO.findByName(name, connection))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> bankServiceImpl.findByName(name))
                    .isInstanceOf(DAOException.class);
        }
    }

    @Nested
    @DisplayName("findAll ()")
    public class findAll {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnAllBanks() throws SQLException, DAOException {
            List<BankResponse> expectedAll = List.of(
                    BankResponse.builder()
                            .id(20L)
                            .name("name1")
                            .build(),
                    BankResponse.builder()
                            .id(22L)
                            .name("name2")
                            .build()
            );
            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(bankDAO.findAll(connection))
                    .thenReturn(
                            expectedAll.stream()
                                    .map(r -> Bank.builder()
                                            .id(r.getId())
                                            .name(r.getName())
                                            .build()
                                    ).toList()
                    );
            Mockito.when(bankMapper.toResponse(Mockito.any()))
                    .thenAnswer(invocation -> {
                        Bank bank = invocation.getArgument(0, Bank.class);
                        for (BankResponse bankResponse : expectedAll) {
                            if (bank.getId().equals(bankResponse.getId())) {
                                return bankResponse;
                            }
                        }
                        return Bank.builder().build();
                    });

            List<BankResponse> actualAll = bankServiceImpl.findAll();
            assertThat(actualAll).containsExactlyInAnyOrderElementsOf(expectedAll);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> bankServiceImpl.findAll())
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(bankDAO.findAll(connection))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> bankServiceImpl.findAll())
                    .isInstanceOf(DAOException.class);
        }
    }
}
