package ru.creditbank.apigateway.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.creditbank.apigateway.exception.UserAlreadyExistsException;
import ru.creditbank.apigateway.exception.UserDoesNotExistsException;
import ru.creditbank.apigateway.exception.WrongOrInvalidJwtTokenException;
import ru.creditbank.apigateway.exception.WrongPasswordException;

import static jakarta.servlet.http.HttpServletResponse.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorResponseWriter errorResponseWriter;

    @ExceptionHandler(Exception.class)
    public void handle(Exception e, HttpServletResponse response) {
        doHandle(e, response, SC_BAD_REQUEST, "Bad request");
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public void handle(UserAlreadyExistsException e, HttpServletResponse response) {
        doHandle(e, response, SC_CONFLICT, "User already exists");
    }

    @ExceptionHandler(UserDoesNotExistsException.class)
    public void handle(UserDoesNotExistsException e, HttpServletResponse response) {
        doHandle(e, response, SC_NOT_FOUND, "User does not exists");
    }

    @ExceptionHandler(WrongPasswordException.class)
    public void handle(WrongPasswordException e, HttpServletResponse response) {
        doHandle(e, response, SC_UNAUTHORIZED, "Wrong password");
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public void handle(AuthorizationDeniedException e, HttpServletResponse response) {
        doHandle(e, response, SC_FORBIDDEN, "Forbidden");
    }

    @ExceptionHandler(WrongOrInvalidJwtTokenException.class)
    public void handle(WrongOrInvalidJwtTokenException e, HttpServletResponse response) {
        doHandle(e, response, SC_UNAUTHORIZED, "Invalid token");
    }

    private void doHandle(Exception e, HttpServletResponse response, int status, String message) {
        log.warn(e.toString());
        errorResponseWriter.sendError(response, status, message);
    }
}
