package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;

public class CreditStatusUpdateException extends BusinessException {
    public CreditStatusUpdateException(String creditMustBePending, HttpStatus httpStatus) {
        super(creditMustBePending, httpStatus);
    }
}
