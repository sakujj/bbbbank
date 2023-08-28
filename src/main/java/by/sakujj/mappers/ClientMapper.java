package by.sakujj.mappers;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.hashing.BCryptHasher;
import by.sakujj.hashing.Hasher;
import by.sakujj.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class ClientMapper {
    private static final Hasher hasher = BCryptHasher.getInstance();
    private static final ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    public static ClientMapper getInstance() {
        return INSTANCE;
    }

    public Client fromRequest(ClientRequest clientRequest) {
        String email = clientRequest.getEmail();
        String username = clientRequest.getUsername();
        String notHashedPassword = clientRequest.getNotHashedPassword();
        String password = hasher.hash(notHashedPassword);

        return Client.builder()
                .email(email)
                .username(username)
                .password(password)
                .build();
    }

    public abstract ClientResponse toResponse(Client client);
}
