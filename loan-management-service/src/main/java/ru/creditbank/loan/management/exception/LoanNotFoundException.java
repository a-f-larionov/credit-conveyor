package ru.creditbank.loan.management.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

import java.util.UUID;

import static java.lang.String.format;

public class LoanNotFoundException extends BusinessException {
    public LoanNotFoundException(UUID loanId) {
        super(format("Loan with id %s not found", loanId), HttpStatus.NOT_FOUND);
    }
}
