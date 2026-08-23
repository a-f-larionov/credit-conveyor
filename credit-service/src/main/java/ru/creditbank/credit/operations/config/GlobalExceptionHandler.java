package ru.creditbank.credit.operations.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorResponseWriter errorResponseWriter;

    @ExceptionHandler(Exception.class)
    public void handle(Exception e, HttpServletResponse response) {
        doHandle(e, response, SC_BAD_REQUEST, "Unknown Error");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handle(MethodArgumentNotValidException e, HttpServletResponse response) {
        doHandle(e, response, SC_BAD_REQUEST, "Bad request");
    }

    private void doHandle(Exception e, HttpServletResponse response, int status, String errMsg) {
        log.warn(e.toString());
        errorResponseWriter.sendError(response, status, errMsg);
    }
}
