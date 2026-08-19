package ru.creditbank.apigateway.exception;

public class WrongOrInvalidJwtTokenException extends RuntimeException {

    public WrongOrInvalidJwtTokenException(String msg) {
        super(msg);
    }
}