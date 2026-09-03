package ru.creditbank.loan.management.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

public class PaymentException extends BusinessException {
    public PaymentException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
