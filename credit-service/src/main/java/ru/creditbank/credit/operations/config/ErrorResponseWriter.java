package ru.creditbank.credit.operations.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ErrorResponseWriter {

    private final ObjectMapper objectMapper;

    public void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, String> errorBody = Map.of("error", message);
        objectMapper.writeValue(response.getWriter(), errorBody);

        response.getWriter().flush();
    }
}
