package ru.creditbank.credit.operations.exception;

public class WrongOrInvalidJwtTokenException extends RuntimeException {

    public WrongOrInvalidJwtTokenException(String msg) {
        super(msg);
    }
}