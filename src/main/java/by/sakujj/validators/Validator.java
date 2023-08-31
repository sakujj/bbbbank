package by.sakujj.validators;

import by.sakujj.exceptions.ValidationException;
import by.sakujj.model.Entity;

public interface Validator<T> {
    void validate(T obj) throws ValidationException;
}
