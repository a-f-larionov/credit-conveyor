package ru.creditbank.apigateway.exceptions;

public class WrongOrInvalidJwtTokenException extends RuntimeException {

    public WrongOrInvalidJwtTokenException(String msg) {
        super(msg);
    }
}