package by.sakujj.services;

import by.sakujj.connection.ConnectionPool;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.mappers.ClientMapper;
import by.sakujj.model.Client;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClientServiceImpl implements ClientService {
    private final ConnectionPool connectionPool;
    private final ClientMapper clientMapper;
    private final ClientDAO clientDAO;

    @SneakyThrows
    public List<ClientResponse> findAll(){
        try(Connection connection = connectionPool.getConnection()) {
            List<ClientResponse> clientResponses = clientDAO.findAll(connection)
                    .stream()
                    .map(clientMapper::toResponse)
                    .toList();
            return clientResponses;
        }
    }

    @SneakyThrows
    public Optional<ClientResponse> findByEmail(String email){
        try(Connection connection = connectionPool.getConnection()) {
            Optional<Client> client = clientDAO.findByEmail(email, connection);

            return client.map(c -> clientMapper.toResponse(c));
        }
    }

    @SneakyThrows
    public Optional<ClientResponse> findById(Long id){
        try(Connection connection = connectionPool.getConnection()) {
            Optional<Client> client = clientDAO.findById(id, connection);

            return client.map(c -> clientMapper.toResponse(c));
        }
    }

    @SneakyThrows
    public Long save(ClientRequest request) {
        try (Connection connection = connectionPool.getConnection()) {
            Client client = clientMapper.fromRequest(request);

            return clientDAO.save(client, connection);
        }
    }
}

