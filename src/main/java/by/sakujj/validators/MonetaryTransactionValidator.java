package by.sakujj.validators;

import by.sakujj.dto.AccountResponse;
import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.exceptions.ValidationException;
import by.sakujj.model.Account;
import by.sakujj.model.Currency;
import by.sakujj.model.MonetaryTransactionType;
import by.sakujj.services.AccountService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

import static by.sakujj.model.MonetaryTransactionType.DEPOSIT;

@RequiredArgsConstructor
public class MonetaryTransactionValidator implements Validator<MonetaryTransactionRequest> {

    private final AccountService accountService;

    @Override
    public void validate(MonetaryTransactionRequest obj) throws ValidationException {
        Objects.requireNonNull(obj);
        List<String> errors = new ArrayList<>();

        String moneyAmountString = obj.getMoneyAmount();
        Objects.requireNonNull(moneyAmountString);
        BigDecimal moneyAmount = null;
        try {
            if (moneyAmountString.lastIndexOf(".") > 20 || moneyAmountString.length() > 23)
                throw new NumberFormatException();

            moneyAmount = new BigDecimal(moneyAmountString);
        } catch (NumberFormatException e) {
            errors.add("Wrong money amount format, should be: [0-9]{1,20}.[0-9]{0,2}");
        }

        String typeString = obj.getType();
        Objects.requireNonNull(typeString);
        try {
            MonetaryTransactionType type = MonetaryTransactionType.valueOf(typeString);

            Optional<String> possibleReceiverAccountId = obj.getReceiverAccountId();
            Optional<String> possibleSenderAccountId = obj.getSenderAccountId();
            Objects.requireNonNull(possibleReceiverAccountId);
            Objects.requireNonNull(possibleSenderAccountId);
            validateAccountIds(type,
                    possibleSenderAccountId,
                    possibleReceiverAccountId,
                    moneyAmount,
                    errors);
        } catch (IllegalArgumentException e) {
            errors.add("Wrong TRANSACTION type specified");
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);

    }

    private void validateDepositAccountId(Optional<String> possibleReceiverAccountId,
                                          List<String> errors) {

        String receiverAccountId = validatePresenceAndReturnNullable(
                possibleReceiverAccountId,
                "You did not specify the receiver",
                errors
        );
        if (receiverAccountId == null)
            return;

        Optional<AccountResponse> possibleReceiverAccount
                = accountService.findById(receiverAccountId);
        validatePresenceAndReturnNullable(
                possibleReceiverAccount,
                "Specified account to DEPOSIT to does not exist",
                errors
        );
    }

    private void validateWithdrawalAccountId(Optional<String> possibleSenderAccountId,
                                             BigDecimal moneyAmount,
                                             List<String> errors) {
        String senderAccountId = validatePresenceAndReturnNullable(
                possibleSenderAccountId,
                "You did not specify the sender",
                errors
        );
        if (senderAccountId == null)
            return;

        Optional<AccountResponse> possibleSenderAccount
                = accountService.findById(senderAccountId);
        AccountResponse senderAccount = validatePresenceAndReturnNullable(
                possibleSenderAccount,
                "Specified account to WITHDRAW from does not exist",
                errors
        );
        if (senderAccount == null)
            return;

        validateAccountHasEnoughMoney(
                senderAccount,
                moneyAmount,
                "Not enough money to WITHDRAW",
                errors);

    }

    private void validateTransferAccountIds(
            Optional<String> possibleSenderAccountId,
            Optional<String> possibleReceiverAccountId,
            BigDecimal moneyAmount,
            List<String> errors) {
        String senderAccountId = validatePresenceAndReturnNullable(
                possibleSenderAccountId,
                "You did not specify the sender",
                errors);

        String receiverAccountId = validatePresenceAndReturnNullable(
                possibleReceiverAccountId,
                "You did not specify the receiver",
                errors);

        if (senderAccountId == null || receiverAccountId == null)
            return;

        if (receiverAccountId.equals(senderAccountId)) {
            errors.add("You can not TRANSFER to the same account");
        }

        Optional<AccountResponse> possibleSenderAccount = accountService.findById(senderAccountId);
        AccountResponse senderAccount = validatePresenceAndReturnNullable(
                possibleSenderAccount,
                "Specified account to TRANSFER from does not exist",
                errors
        );

        Optional<AccountResponse> possibleReceiverAccount = accountService.findById(receiverAccountId);
        AccountResponse receiverAccount = validatePresenceAndReturnNullable(
                possibleReceiverAccount,
                "Specified account to TRANSFER to does not exist",
                errors
        );


        if (senderAccount == null || receiverAccount == null) {
            return;
        }

        validateAccountsHaveSameCurrency(senderAccount, receiverAccount,
                "You can not TRANSFER from %s account to %s account"
                        .formatted(senderAccount.getCurrency().toString(),
                                receiverAccount.getCurrency().toString()),
                errors);

        validateAccountHasEnoughMoney(
                senderAccount,
                moneyAmount,
                "Not enough money to TRANSFER",
                errors);
    }

    private <T> T validatePresenceAndReturnNullable(Optional<T> optional,
                                                    String errorMsg,
                                                    List<String> errors) {
        Objects.requireNonNull(optional);
        Objects.requireNonNull(errorMsg);
        Objects.requireNonNull(errors);

        if (optional.isEmpty()) {
            errors.add(errorMsg);
            return null;
        }
        return optional.get();
    }

    private void validateAccountHasEnoughMoney(AccountResponse account,
                                               BigDecimal requiredAmount,
                                               String errorMsg,
                                               List<String> errors) {
        Objects.requireNonNull(account);
        Objects.requireNonNull(errorMsg);
        Objects.requireNonNull(errors);

        if (requiredAmount != null) {
            if (account.getMoneyAmount().compareTo(requiredAmount) < 0) {
                errors.add(errorMsg);
            }
        }
    }

    private void validateAccountsHaveSameCurrency(
            AccountResponse first,
            AccountResponse second,
            String errorMsg,
            List<String> errors) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);

        if (first.getCurrency() != second.getCurrency()) {
            errors.add(errorMsg);
        }
    }

    public void validateAccountIds(MonetaryTransactionType type,
                                   Optional<String> possibleSenderAccountId,
                                   Optional<String> possibleReceiverAccountId,
                                   BigDecimal nullableMoneyAmount,
                                   List<String> errors) {
        switch (type) {
            case DEPOSIT -> validateDepositAccountId(
                    possibleReceiverAccountId,
                    errors);
            case WITHDRAWAL -> validateWithdrawalAccountId(
                    possibleSenderAccountId,
                    nullableMoneyAmount,
                    errors);
            case TRANSFER -> validateTransferAccountIds(
                    possibleSenderAccountId,
                    possibleReceiverAccountId,
                    nullableMoneyAmount,
                    errors);
        }
    }

//    private <T> boolean validateBothPresent(Optional<T> first,
//                                            Optional<T> second,
//                                            String firstErrorMsg,
//                                            String secondErrorMsg,
//                                            List<String> errors) {
//        T firstUnwrapped = validatePresenceAndReturnNullable(
//                first,
//                firstErrorMsg,
//                errors);
//
//        T secondUnwrapped = validatePresenceAndReturnNullable(
//                second,
//                secondErrorMsg,
//                errors);
//
//        return firstUnwrapped != null && secondUnwrapped != null;
//    }
}
