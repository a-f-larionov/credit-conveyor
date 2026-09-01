package ru.creditbank.apigateway.exception;

import org.springframework.http.HttpStatus;
import ru.creditbank.common.library.exception.BusinessException;

public class WrongPasswordException extends BusinessException {
    public WrongPasswordException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
