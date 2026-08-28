package ru.creditbank.apigateway.exception;

import org.springframework.http.HttpStatus;

public class WrongPasswordException extends BusinessException {
    public WrongPasswordException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
