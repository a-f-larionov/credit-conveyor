package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;

public class AccessNotAllowed extends RestException {
    public AccessNotAllowed(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
