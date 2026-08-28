package ru.creditbank.common.library.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import ru.creditbank.common.library.config.ErrorResponseWriter;

@AutoConfiguration
public class ExceptionHandlerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ErrorResponseWriter errorResponseWriter(ObjectMapper objectMapper) {
        return new ErrorResponseWriter(objectMapper);
    }

}
