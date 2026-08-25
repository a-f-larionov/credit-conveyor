package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;

public class CreditNotFoundException extends RestException {
    public CreditNotFoundException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
