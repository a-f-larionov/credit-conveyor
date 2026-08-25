package ru.creditbank.apigateway.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RestException {
    public UserAlreadyExistsException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
