package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;

public class AccessNotAllowed extends BusinessException {
    public AccessNotAllowed(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
