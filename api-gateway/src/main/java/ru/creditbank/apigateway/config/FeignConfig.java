package ru.creditbank.apigateway.config;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
@EnableFeignClients(basePackages = "ru.creditbank.apigateway.feign")
public class FeignConfig {

    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                template.header("Authorization", attrs.getRequest().getHeader(HEADER_AUTHORIZATION));
            }
        };
    }
}