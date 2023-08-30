package by.sakujj.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.ClientResponse;
import by.sakujj.hashing.Hasher;
import by.sakujj.mappers.ClientMapper;
import by.sakujj.model.Client;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Connection;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthenticationServiceImpl implements AuthenticationService{

    private final ClientDAO clientDAO;
    private final ClientMapper clientMapper;
    private final ConnectionPool connectionPool;
    private final Hasher hasher;

    @SneakyThrows
    public Optional<ClientResponse> authenticate(AuthenticationService.Credentials credentials) {
        try (Connection connection = connectionPool.getConnection()) {
            Optional<Client> foundClient = clientDAO.findByEmail(credentials.getEmail(), connection);
            if (foundClient.isEmpty())
                return Optional.empty();

            Client client = foundClient.get();
            String password = client.getPassword();
            if (hasher.verifyHash(credentials.getPassword(), password)) {
                return Optional.of(clientMapper.toResponse(client));
            }

            return Optional.empty();
        }
    }
}
