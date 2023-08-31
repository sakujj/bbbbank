package by.sakujj.exceptions;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends Exception {
    private List<String> errors;


    public ValidationException(List<String> errors) {
        super();
        this.errors = errors;
    }
}
