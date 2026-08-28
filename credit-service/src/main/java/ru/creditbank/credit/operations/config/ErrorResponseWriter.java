package ru.creditbank.credit.operations.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.creditbank.credit.operations.dto.rs.ErrorRsDto;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void sendError(HttpServletRequest request, HttpServletResponse response, HttpStatus httpStatus) {
        sendError(request, response, httpStatus, httpStatus.getReasonPhrase());
    }

    @SneakyThrows
    public void sendError(HttpServletRequest request, HttpServletResponse response, HttpStatus httpStatus, String message) {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), buildErrorRsDto(message, httpStatus, request.getRequestURI()));

        response.getWriter().flush();
    }

    private ErrorRsDto buildErrorRsDto(String message, HttpStatus httpStatus, String path) {
        return ErrorRsDto.builder()
                .timestamp(Instant.now())
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
