package ru.creditbank.apigateway.exception;

import org.springframework.http.HttpStatus;

public class UserDoesNotExistsException extends BusinessException {
    public UserDoesNotExistsException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
