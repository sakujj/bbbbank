package test.unit.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.mappers.ClientMapper;
import by.sakujj.model.Client;
import by.sakujj.services.ClientServiceImpl;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ClientServiceImplTests {
    private AutoCloseable mockitoClosable;

    @Mock
    private ConnectionPool connectionPool;

    @Mock
    private Connection connection;

    @Mock
    private ClientDAO clientDAO;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientServiceImpl;

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
            String username = "user1";
            String email = "email1@gmail.com";
            String password = "1233";
            Optional<ClientResponse> expectedResponse = Optional.of(ClientResponse.builder()
                    .id(id)
                    .username(username)
                    .email(email)
                    .build());
            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(clientDAO.findById(id, connection))
                    .thenReturn(Optional.of(Client.builder()
                            .id(id)
                            .username(username)
                            .password(password)
                            .email(email)
                            .build())
                    );
            Mockito.when(clientMapper.toResponse(Mockito.any())).thenReturn(
                    expectedResponse.get()
            );

            Optional<ClientResponse> actual = clientServiceImpl.findById(id);
            assertThat(actual).isPresent();
            assertThat(actual).isEqualTo(expectedResponse);
        }


        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            Long id = 55L;

            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> clientServiceImpl.findById(id))
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            Long id = 55L;

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientDAO.findById(id, connection))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> clientServiceImpl.findById(id))
                    .isInstanceOf(DAOException.class);
        }
    }

    @Nested
    @DisplayName("findByEmail (String)")
    public class findByEmail {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnCorrectResponse() throws SQLException, DAOException {
            Long id = 55L;
            String username = "user1";
            String email = "email1@gmail.com";
            String password = "1233";
            Optional<ClientResponse> expectedResponse = Optional.of(ClientResponse.builder()
                    .id(id)
                    .username(username)
                    .email(email)
                    .build());
            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(clientDAO.findByEmail(Mockito.anyString(), Mockito.any()))
                    .thenReturn(Optional.of(Client.builder()
                            .id(id)
                            .password(password)
                            .email(email)
                            .username(username)
                            .build())
                    );
            Mockito.when(clientMapper.toResponse(Mockito.any())).thenReturn(
                    expectedResponse.get()
            );

            Optional<ClientResponse> actual = clientServiceImpl.findByEmail(email);
            assertThat(actual).isPresent();
            assertThat(actual).isEqualTo(expectedResponse);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException, DAOException {
            String email = "bbank@gmail.com";

            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> clientServiceImpl.findByEmail(email))
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            String email = "bbank@fasfd";

            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientDAO.findByEmail(email, connection))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> clientServiceImpl.findByEmail(email))
                    .isInstanceOf(DAOException.class);
        }
    }

    @Nested
    @DisplayName("findAll ()")
    public class findAll {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldReturnAllBanks() throws SQLException, DAOException {
            List<ClientResponse> expectedAll = List.of(
                    ClientResponse.builder()
                            .id(20L)
                            .username("name1")
                            .email("em@1")
                            .build(),
                    ClientResponse.builder()
                            .id(22L)
                            .username("name2")
                            .email("em@2")
                            .build()
            );
            Mockito.when(connectionPool.getConnection()).thenReturn(connection);
            Mockito.when(clientDAO.findAll(connection))
                    .thenReturn(
                            expectedAll.stream()
                                    .map(r -> Client.builder()
                                            .id(r.getId())
                                            .email(r.getEmail())
                                            .username(r.getUsername())
                                            .build()
                                    ).toList()
                    );
            Mockito.when(clientMapper.toResponse(Mockito.any()))
                    .thenAnswer(invocation -> {
                        Client client = invocation.getArgument(0, Client.class);
                        for (ClientResponse clientResponse : expectedAll) {
                            if (client.getId().equals(clientResponse.getId())) {
                                return clientResponse;
                            }
                        }
                        return Client.builder().build();
                    });

            List<ClientResponse> actualAll = clientServiceImpl.findAll();
            assertThat(actualAll).containsExactlyInAnyOrderElementsOf(expectedAll);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> clientServiceImpl.findAll())
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientDAO.findAll(Mockito.any()))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> clientServiceImpl.findAll())
                    .isInstanceOf(DAOException.class);
        }
    }

    @Nested
    @DisplayName("save (ClientRequest)")
    public class save {
        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldSave() throws SQLException, DAOException {
            ClientRequest clientToSave = ClientRequest.builder()
                    .username("uz1")
                    .email("Em@srafs")
                    .notHashedPassword("password")
                    .build();
            Client expectedClient = Client.builder()
                    .username(clientToSave.getUsername())
                    .email(clientToSave.getEmail())
                    .password(clientToSave.getNotHashedPassword())
                    .id(999L)
                    .build();
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Mockito.when(clientMapper.fromRequest(clientToSave))
                    .thenReturn(expectedClient);
            Mockito.when(clientDAO.save(Mockito.any(), Mockito.any()))
                    .thenReturn(expectedClient.getId());

            Long actualId = clientServiceImpl.save(clientToSave);

            assertThat(actualId).isEqualTo(expectedClient.getId());
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowSQLException() throws SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenThrow(new SQLException());

            assertThatThrownBy(() -> clientServiceImpl.save(ClientRequest.builder().build()))
                    .isInstanceOf(SQLException.class);
        }

        @Test
        @Execution(ExecutionMode.SAME_THREAD)
        void shouldThrowDAOException() throws DAOException, SQLException {
            Mockito.when(connectionPool.getConnection())
                    .thenReturn(connection);
            Client c = Client.builder().build();
            Mockito.when(clientMapper.fromRequest(Mockito.any()))
                    .thenReturn(c);
            Mockito.when(clientDAO.save(c, connection))
                    .thenThrow(new DAOException());

            assertThatThrownBy(() -> clientServiceImpl.save(ClientRequest.builder().build()))
                    .isInstanceOf(DAOException.class);
        }
    }
}
