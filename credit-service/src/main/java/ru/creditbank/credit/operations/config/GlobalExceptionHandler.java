package ru.creditbank.credit.operations.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.creditbank.credit.operations.exception.BusinessException;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorResponseWriter errorResponseWriter;

    @ExceptionHandler(Exception.class)
    public void handle(Exception e, HttpServletResponse response, HttpServletRequest request) {
        log.error(e.toString());
        errorResponseWriter.sendError(request, response, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public void handle(BusinessException e, HttpServletRequest request, HttpServletResponse response) {
        log.warn(e.toString());
        errorResponseWriter.sendError(request, response, e.getHttpStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handle(MethodArgumentNotValidException e, HttpServletRequest request, HttpServletResponse response) {
        var errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + " : " + fe.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining("; "));

        log.warn("Validation failed: {}", errors);

        errorResponseWriter.sendError(request, response, BAD_REQUEST, errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handle(AccessDeniedException e, HttpServletRequest request, HttpServletResponse response) {
        log.warn(e.toString());
        errorResponseWriter.sendError(request, response, FORBIDDEN);
    }
}
