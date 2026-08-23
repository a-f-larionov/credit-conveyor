package ru.creditbank.apigateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.creditbank.apigateway.dto.rs.ErrorRsDto;

@Component
@RequiredArgsConstructor
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public void sendError(HttpServletResponse response, int status, String message) {
        response.setStatus(status);
        response.setContentType("application/json");

        objectMapper.writeValue(response.getWriter(), buildErrorRsDto(message));

        response.getWriter().flush();
    }

    private ErrorRsDto buildErrorRsDto(String message) {
        return ErrorRsDto.builder()
                .error(message)
                .build();
    }
}
