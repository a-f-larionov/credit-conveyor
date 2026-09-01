package ru.creditbank.credit.operations.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

public class AccessNotAllowed extends BusinessException {
    public AccessNotAllowed(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
