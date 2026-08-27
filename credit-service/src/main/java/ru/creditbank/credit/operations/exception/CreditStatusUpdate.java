package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;

public class CreditStatusUpdate extends RestException {
    public CreditStatusUpdate(String creditMustBePending, HttpStatus httpStatus) {
        super(creditMustBePending, httpStatus);
    }
}
