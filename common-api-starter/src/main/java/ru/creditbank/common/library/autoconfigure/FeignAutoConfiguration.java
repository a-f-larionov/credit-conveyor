package ru.creditbank.common.library.autoconfigure;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

import java.nio.charset.StandardCharsets;

@AutoConfiguration
@Slf4j
public class FeignAutoConfiguration {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            var context = SecurityContextHolder.getContext();
            if (context != null) {
                var jwtToken = context.getAuthentication().getCredentials();
                if (jwtToken != null) {
                    template.header(HEADER_AUTHORIZATION, BEARER_PREFIX + jwtToken);
                }
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor loggingInterceptor() {
        return (template) -> {
            String bodyString = "";
            if (template.body() != null) {
                bodyString = new String(template.body(), StandardCharsets.UTF_8);
            }
            log.info("Feign request: {} {} {}", template.method(), template.url(), bodyString);
        };
    }
}
