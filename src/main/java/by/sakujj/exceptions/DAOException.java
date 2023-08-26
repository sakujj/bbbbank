package by.sakujj.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DaoException extends Exception{
    public DaoException (Throwable cause) {
        super(cause);
    }

    public DaoException (String message) {
        super(message);
    }

    public DaoException (String message, Throwable cause) {
        super(message, cause);
    }
}
