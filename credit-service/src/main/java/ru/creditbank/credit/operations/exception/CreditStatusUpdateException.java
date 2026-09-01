package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

public class CreditStatusUpdateException extends BusinessException {
    public CreditStatusUpdateException(String creditMustBePending, HttpStatus httpStatus) {
        super(creditMustBePending, httpStatus);
    }
}
