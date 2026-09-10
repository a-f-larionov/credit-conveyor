package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

public class AutoDecisionException extends BusinessException {
    public AutoDecisionException(String msg) {
        super(msg, HttpStatus.NOT_FOUND);
    }
}
