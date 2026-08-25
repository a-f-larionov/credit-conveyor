package ru.creditbank.apigateway.exception;

import org.springframework.http.HttpStatus;

public class WrongOrInvalidJwtTokenException extends RestException {
    public WrongOrInvalidJwtTokenException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}