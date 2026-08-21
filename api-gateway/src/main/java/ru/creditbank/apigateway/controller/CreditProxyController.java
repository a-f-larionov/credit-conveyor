package ru.creditbank.apigateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/credit-service/api/v1/")
@RequiredArgsConstructor
public class CreditProxyController {

    @Value("${services.credit-service.url}")
    private String creditServiceUrl;

    private final RestTemplate restTemplate;

    @PostMapping("/create")
    public ResponseEntity<?> createCredit(@RequestBody Object requestBody) {

        return restTemplate.exchange(
                creditServiceUrl + "/credit-service/api/v1/create",
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                Object.class
        );
    }
}
