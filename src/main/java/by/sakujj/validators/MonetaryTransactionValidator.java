package by.sakujj.validators;

import by.sakujj.dto.MonetaryTransactionRequest;
import by.sakujj.dto.MonetaryTransactionResponse;
import by.sakujj.exceptions.ValidationException;
import by.sakujj.model.MonetaryTransaction;
import by.sakujj.model.MonetaryTransactionType;
import by.sakujj.services.AccountService;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
public class MonetaryTransactionValidator implements Validator<MonetaryTransactionRequest> {

    private final AccountService accountService;

    @Override
    public void validate(MonetaryTransactionRequest obj) throws ValidationException {
        Objects.requireNonNull(obj);
        List<String> errors = new ArrayList<>();

        String moneyAmount = obj.getMoneyAmount();
        Objects.requireNonNull(moneyAmount);
        try {
            if (moneyAmount.lastIndexOf(".") > 20 || moneyAmount.length() > 23)
                throw new NumberFormatException();

            BigDecimal money = new BigDecimal(moneyAmount);
        } catch (NumberFormatException e) {
            errors.add("Wrong money amount format, should be: [0-9]{1,20}.[0-9]{0,2}");
        }

        String typeString = obj.getType();
        Objects.requireNonNull(typeString);
        try {
            MonetaryTransactionType type = MonetaryTransactionType.valueOf(typeString);

            Optional<String> possibleSenderAccountId = obj.getSenderAccountId();
            Optional<String> possibleReceiverAccountId = obj.getReceiverAccountId();
            Objects.requireNonNull(possibleSenderAccountId);
            Objects.requireNonNull(possibleReceiverAccountId);
            switch (type) {
                case DEPOSIT -> validatePossibleReceiverAccountId(
                        possibleReceiverAccountId,
                        errors);
                case WITHDRAWAL -> validatePossibleSenderAccountId(
                        possibleSenderAccountId,
                        errors);
                case TRANSFER -> {
                    validatePossibleReceiverAccountId(
                            possibleReceiverAccountId,
                            errors);
                    validatePossibleSenderAccountId(
                            possibleSenderAccountId,
                            errors);
                }
            }
        } catch (IllegalArgumentException e) {
            errors.add("Wrong TRANSACTION type specified");
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }

    private void validatePossibleReceiverAccountId(Optional<String> possibleReceiverAccountId,
                                                   List<String> errors) {
        if (possibleReceiverAccountId.isEmpty()) {
            errors.add("You did not specify the receiver");
        } else if (
                accountService.findById(possibleReceiverAccountId.get())
                        .isEmpty()
        ) {
            errors.add("Specified receiver does not exist");
        }
    }

    private void validatePossibleSenderAccountId(Optional<String> possibleSenderAccountId,
                                                 List<String> errors) {
        if (possibleSenderAccountId.isEmpty()) {
            errors.add("You did not specify the sender");
        } else if (
                accountService.findById(possibleSenderAccountId.get())
                        .isEmpty()
        ) {
            errors.add("Specified sender does not exist");
        }
    }
}
