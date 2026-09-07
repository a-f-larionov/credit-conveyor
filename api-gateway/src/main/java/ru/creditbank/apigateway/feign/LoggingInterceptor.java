package ru.creditbank.apigateway.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LoggingInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String bodyString = "";
        if (template.body() != null) {
            byte[] body = template.body();
            bodyString = new String(body, StandardCharsets.UTF_8);
        }
        log.info("Feign request: {} {} {} ", template.method(), template.url(), bodyString);
    }
}