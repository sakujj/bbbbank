package by.sakujj.services;

import by.sakujj.dto.BankResponse;

import java.util.List;
import java.util.Optional;

public interface BankService {
    List<BankResponse> findAll();
    Optional<BankResponse> findByName(String name);
    Optional<BankResponse> findById(Long id);
}
