package ru.creditbank.apigateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.creditbank.apigateway.dto.rs.ErrorRsDto;

@Component
@RequiredArgsConstructor
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void sendError(HttpServletResponse response, HttpStatus httpStatus) {
        sendError(response, httpStatus, httpStatus.getReasonPhrase());
    }

    @SneakyThrows
    public void sendError(HttpServletResponse response, HttpStatus httpStatus, String message) {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), buildErrorRsDto(message));

        response.getWriter().flush();
    }

    private ErrorRsDto buildErrorRsDto(String message) {
        return ErrorRsDto.builder()
                .message(message)
                .build();
    }
}
