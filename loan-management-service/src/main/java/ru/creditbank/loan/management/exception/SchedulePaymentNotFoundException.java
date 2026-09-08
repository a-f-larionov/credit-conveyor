package ru.creditbank.loan.management.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

import java.util.UUID;

import static java.lang.String.format;

public class SchedulePaymentNotFoundException extends BusinessException {

    public SchedulePaymentNotFoundException(UUID paymentId) {
        super(format("Schedule payment with id %s not found", paymentId), HttpStatus.NOT_FOUND);
    }
}
