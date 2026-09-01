package ru.creditbank.apigateway.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

public class UserAlreadyExistsException extends BusinessException {
    public UserAlreadyExistsException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
