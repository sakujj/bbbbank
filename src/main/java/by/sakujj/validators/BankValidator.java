package by.sakujj.validators;

import by.sakujj.dto.BankRequest;
import by.sakujj.exceptions.ValidationException;
import by.sakujj.services.BankService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class BankValidator implements Validator<BankRequest> {
    private final BankService bankService;

    @Override
    public void validate(BankRequest obj) throws ValidationException {
        Objects.requireNonNull(obj);

        List<String> errors = new ArrayList<>();

        String idString = obj.getId();
        Objects.requireNonNull(idString);
        Long id;
        try {
            if (idString.length() != 11)
                throw new NumberFormatException();

            id = Long.parseLong(idString);
            if (bankService.findById(id).isPresent()) {
                errors.add("Bank with specified ID already exists");
            }
        } catch (NumberFormatException e) {
            errors.add("Wrong ID format, should be: [0-9]{11}");
        }


        String name = obj.getName();
        Objects.requireNonNull(name);
        if (bankService.findByName(name).isPresent()) {
            errors.add("Bank with specified name already exists");
        }
        long length = name
                .strip()
                .codePoints()
                .count();
        if (length < 1) {
            errors.add("Bank's name is too short");
        } else if (length > 30) {
            errors.add("Bank's name should be less than 31 character");
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
