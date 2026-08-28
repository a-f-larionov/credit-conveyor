package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

import static java.lang.String.format;

public class CreditNotFoundException extends BusinessException {
    public CreditNotFoundException(UUID creditId) {
        super(format("Credit with id %s not found", creditId), HttpStatus.NOT_FOUND);
    }
}
