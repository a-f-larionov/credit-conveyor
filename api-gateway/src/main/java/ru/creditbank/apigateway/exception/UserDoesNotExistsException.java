package ru.creditbank.apigateway.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

public class UserDoesNotExistsException extends BusinessException {
    public UserDoesNotExistsException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
