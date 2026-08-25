package ru.creditbank.apigateway.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.creditbank.apigateway.exception.RestException;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorResponseWriter errorResponseWriter;

    @ExceptionHandler(Exception.class)
    public void handle(Exception e, HttpServletResponse response) {
        log.error(e.toString());
        errorResponseWriter.sendError(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RestException.class)
    public void handle(RestException e, HttpServletResponse response) {
        log.warn(e.toString());
        errorResponseWriter.sendError(response, e.getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handle(MethodArgumentNotValidException e, HttpServletResponse response) {
        var errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " 4: " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", errors);

        errorResponseWriter.sendError(response, BAD_REQUEST, errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handle(AccessDeniedException e, HttpServletResponse response) {
        log.warn(e.toString());
        errorResponseWriter.sendError(response, FORBIDDEN);
    }
}
