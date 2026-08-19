package ru.creditbank.apigateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.creditbank.apigateway.entitiy.UserModel;

@RestController
@RequestMapping("/credit-service/api/v1/")
@RequiredArgsConstructor
public class CreditProxyController {

    @Value("${services.credit-service.url}")
    private String creditServiceUrl;

    private final RestTemplate restTemplate;

    @PostMapping("/create")
    public ResponseEntity<?> createCredit(@RequestBody Object requestBody) {

        var user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var headers = new HttpHeaders();
        headers.set("X-User-Id", String.valueOf(user.getId()));

        var httpEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(
                creditServiceUrl + "/credit-service/api/v1/create",
                HttpMethod.POST,
                httpEntity,
                Object.class
        );
    }
}
