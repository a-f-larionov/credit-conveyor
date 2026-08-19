package ru.creditbank.apigateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfig {

    @Autowired
    RestTemplate restTemplate;

    @Bean
    MockRestServiceServer mockRestServiceServer(RestTemplate restTemplate) {
        return MockRestServiceServer.createServer(restTemplate);
    }
}
