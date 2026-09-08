package ru.creditbank.common.library.autoconfigure;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;

@AutoConfiguration
@Slf4j
public class FeignAutoConfiguration {

    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                template.header("Authorization", attrs.getRequest().getHeader(HEADER_AUTHORIZATION));
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
