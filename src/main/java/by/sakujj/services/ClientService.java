package by.sakujj.services;

import by.sakujj.dto.ClientRequest;
import by.sakujj.dto.ClientResponse;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<ClientResponse> findAll();
    Optional<ClientResponse> findByEmail(String email);
    Optional<ClientResponse> findById(Long id);
    Long save(ClientRequest request);
}
