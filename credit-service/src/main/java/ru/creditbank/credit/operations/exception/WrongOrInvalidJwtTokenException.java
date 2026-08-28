package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;

public class WrongOrInvalidJwtTokenException extends BusinessException {
    public WrongOrInvalidJwtTokenException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}