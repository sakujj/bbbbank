package by.sakujj.services;

import by.sakujj.dto.BankResponse;

import java.util.List;

public interface BankService {
    List<BankResponse> findAll();
}
