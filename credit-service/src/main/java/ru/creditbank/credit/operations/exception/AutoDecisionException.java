package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

import java.util.UUID;

import static java.lang.String.format;

public class AutoDecisionException extends BusinessException {
    public AutoDecisionException(String msg) {
        super(msg, HttpStatus.NOT_FOUND);
    }
}
