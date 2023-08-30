package by.sakujj.services;

import by.sakujj.dto.AccountRequest;
import by.sakujj.dto.AccountResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    boolean updateMoneyAmountById(BigDecimal moneyAmount, String id);
    Optional<AccountResponse> findById(String id);
    List<AccountResponse> findAll();
    List<AccountResponse> findAllByClientId(Long clientId);
    String save(AccountRequest request);
}
