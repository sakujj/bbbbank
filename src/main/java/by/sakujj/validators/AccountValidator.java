package by.sakujj.validators;

import by.sakujj.dto.AccountRequest;
import by.sakujj.exceptions.ValidationException;
import by.sakujj.model.Currency;
import by.sakujj.services.AccountService;
import by.sakujj.services.BankService;
import by.sakujj.services.ClientService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class AccountValidator implements Validator<AccountRequest> {
    private final BankService bankService;
    private final AccountService accountService;
    private final ClientService clientService;

    @Override
    public void validate(AccountRequest obj) throws ValidationException {
        Objects.requireNonNull(obj);

        List<String> errors = new ArrayList<>();
        String bankIdString = obj.getBankId();
        Objects.requireNonNull(bankIdString);
        Long bankId;
        try {
            if (bankIdString.length() != 11)
                throw new NullPointerException();

            bankId = Long.parseLong(bankIdString);
            if (bankService.findById(bankId).isEmpty()) {
                errors.add("Bank with specified ID does not exits");
            }
        } catch (NumberFormatException e) {
            errors.add("Wrong bank ID format, should be: [0-9]{11}");
        }

        String currencyString = obj.getCurrency();
        Objects.requireNonNull(currencyString);
        try {
            Currency currency = Currency.valueOf(currencyString);
        } catch (IllegalArgumentException e) {
            errors.add("Wrong currency entered, should be one of: "
                    + Arrays.toString(Currency.values()));
        }

        String clientEmail = obj.getClientEmail();
        if (clientService.findByEmail(clientEmail).isEmpty()) {
            errors.add("Non existent client ID specified");
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
