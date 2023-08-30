package by.sakujj.services;

import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;

import java.util.Optional;

public interface MonetaryTransactionService extends AutoCloseable{
    Optional<MonetaryTransactionResponse> createDepositTransaction(MonetaryTransactionRequest request);
    Optional<MonetaryTransactionResponse> createWithdrawalTransaction(MonetaryTransactionRequest request);
    Optional<MonetaryTransactionResponse> createTransferTransaction(MonetaryTransactionRequest request);

}
