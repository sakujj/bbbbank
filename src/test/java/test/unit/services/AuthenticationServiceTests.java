package test.unit.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.AccountDAO;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.AccountResponse;
import by.sakujj.dto.ClientResponse;
import by.sakujj.exceptions.DAOException;
import by.sakujj.hashing.BCryptHasher;
import by.sakujj.mappers.AccountMapper;
import by.sakujj.mappers.ClientMapper;
import by.sakujj.model.Account;
import by.sakujj.model.Client;
import by.sakujj.services.AccountServiceImpl;
import by.sakujj.services.AuthenticationService;
import by.sakujj.services.AuthenticationServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthenticationServiceTests {
    private AutoCloseable mockitoClosable;

    @Mock
    private ConnectionPool connectionPool;

    @Mock
    private Connection connection;

    @Mock
    private ClientDAO clientDAO;

    @Mock
    private BCryptHasher hasher;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private AuthenticationServiceImpl authenticationServiceImpl;

    @BeforeEach
    void mockitoSetup() {
        mockitoClosable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void mockitoCleanup() throws Exception {
        mockitoClosable.close();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    void shouldAuthenticate() throws DAOException {
        Mockito.when(clientDAO.findByEmail(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.ofNullable(Client.builder().build()));
        Mockito.when(hasher.verifyHash(Mockito.any(), Mockito.any()))
                .thenReturn(true);
        Mockito.when(clientMapper.toResponse(Mockito.any()))
                .thenReturn(ClientResponse.builder().build());

        Optional<ClientResponse> actual = authenticationServiceImpl.authenticate(new AuthenticationService.Credentials("em", "pass"));

        assertThat(actual).isPresent();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    void shouldNotAuthenticateWrongPassword() throws DAOException {
        Mockito.when(clientDAO.findByEmail(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.ofNullable(Client.builder().build()));
        Mockito.when(hasher.verifyHash(Mockito.any(), Mockito.any()))
                .thenReturn(false);

        Optional<ClientResponse> actual = authenticationServiceImpl.authenticate(new AuthenticationService.Credentials("em", "pass"));

        assertThat(actual).isEmpty();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    void shouldNotAuthenticateNonExistingEmail() throws DAOException {
        Mockito.when(clientDAO.findByEmail(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());

        Optional<ClientResponse> actual = authenticationServiceImpl.authenticate(new AuthenticationService.Credentials("em", "pass"));

        assertThat(actual).isEmpty();
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    void shouldThrowRuntimeExceptionWrongDAO() throws DAOException {
        Mockito.when(clientDAO.findByEmail(Mockito.any(), Mockito.any()))
                .thenThrow(DAOException.class);

        assertThatThrownBy(() ->
                authenticationServiceImpl.authenticate(new AuthenticationService.Credentials("em", "pass")))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @Execution(ExecutionMode.SAME_THREAD)
    void shouldThrowRuntimeExceptionWrongConnection() throws SQLException {
        Mockito.when(connectionPool.getConnection())
                .thenThrow(new SQLException());

        assertThatThrownBy(() -> authenticationServiceImpl.authenticate(new AuthenticationService.Credentials("em", "pass")))
                .isInstanceOf(RuntimeException.class);
    }
}
