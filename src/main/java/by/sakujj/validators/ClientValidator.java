package by.sakujj.validators;

import by.sakujj.application.Application;
import by.sakujj.connection.ConnectionPool;
import by.sakujj.context.ApplicationContext;
import by.sakujj.dao.ClientDAO;
import by.sakujj.dto.ClientRequest;
import by.sakujj.exceptions.DAOException;
import by.sakujj.exceptions.ValidationException;
import by.sakujj.services.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

@RequiredArgsConstructor
public class ClientValidator implements Validator<ClientRequest> {
    private final ClientService clientService;

    @Override
    @SneakyThrows
    public void validate(ClientRequest obj) throws ValidationException {
        Objects.requireNonNull(obj);

        List<String> errors = new ArrayList<>();

        String email = obj.getEmail();
        Objects.requireNonNull(email);

        if (clientService.findByEmail(email).isPresent()) {
            errors.add("Email already exists");
        }
        if (email.length() < 3) {
            errors.add("Email length is too small");
        } else if (email.length() > 100) {
            errors.add("Email length is too large");
        }

        String username = obj.getUsername();
        Objects.requireNonNull(username);

        if (username.isEmpty() || username.isBlank()) {
            errors.add("You should enter your username");
        }
        OptionalInt possibleWhitespace = username.codePoints()
                .filter(Character::isWhitespace)
                .findAny();
        if (possibleWhitespace.isPresent()) {
            errors.add("Username should not contain whitespaces");
        }

        String password = obj.getNotHashedPassword();
        Objects.requireNonNull(password);

        if (password.codePoints().count() < 4) {
            errors.add("Your password should be at least 4 characters long");
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);
    }
}
