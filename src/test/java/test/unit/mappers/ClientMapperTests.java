package test.unit.mappers;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;
import by.sakujj.hashing.BCryptHasher;
import by.sakujj.hashing.Hasher;
import by.sakujj.mappers.ClientMapper;
import by.sakujj.model.Client;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ClientMapperTests {
    private static final ClientMapper clientMapper = ClientMapper.getInstance();
    private static final Hasher hasher = BCryptHasher.getInstance();

    @ParameterizedTest
    @MethodSource
    void toClient(ClientRequest request, Client expected) {
        Client actual = clientMapper.fromRequest(request);

        assertThat(actual.getUsername()).isEqualTo(expected.getUsername());
        assertThat(actual.getEmail()).isEqualTo(expected.getEmail());
        boolean hashVerified = hasher.verifyHash(request.getNotHashedPassword(), actual.getPassword());
        assertThat(hashVerified).isTrue();
    }

    static Stream<Arguments> toClient() {
        final String username1 = "c1";
        final String password1 = "pass_1";
        final String email1 = "email123@gmail.com";
        return Stream.of(
                arguments(
                      ClientRequest.builder()
                              .username(username1)
                              .email(email1)
                              .notHashedPassword(password1)
                              .build(),
                        Client.builder()
                                .username(username1)
                                .email(email1)
                                .build()
                ),
                arguments(
                        ClientRequest.builder()
                                .username(username1 + "XXX")
                                .email(email1 + "FFF")
                                .notHashedPassword(password1 + "CCC")
                                .build(),
                        Client.builder()
                                .username(username1 + "XXX")
                                .email(email1 + "FFF")
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void toClientResponse(Client client, ClientResponse expected) {
        ClientResponse actual = clientMapper.toResponse(client);

        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> toClientResponse() {
        final String name = "client1";
        final String email = "email1";
        final Long id = 342L;
        return Stream.of(
                arguments(
                    Client.builder()
                            .username(name)
                            .email(email)
                            .id(id)
                            .build(),
                    ClientResponse.builder()
                            .username(name)
                            .email(email)
                            .id(id.toString())
                            .build()
                )
        );
    }
}
