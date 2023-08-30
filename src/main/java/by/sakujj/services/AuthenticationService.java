package by.sakujj.services;

import by.sakujj.dto.ClientResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

public interface AuthenticationService {
    @Getter
    @Setter
    @AllArgsConstructor
    class Credentials {
        private String email;
        private String password;
    }

    Optional<ClientResponse> authenticate(Credentials credentials);
}
