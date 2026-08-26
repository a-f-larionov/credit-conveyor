package ru.creditbank.apigateway.controller.proxy;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.apigateway.feign.CreditServiceClient;

@RestController
@RequestMapping("/credit-service/api/v1/")
@RequiredArgsConstructor
public class CreditProxyController {

    private final CreditServiceClient creditServiceClient;

    @PostMapping("/create")
    public JsonNode createCredit(@RequestBody JsonNode requestBody) {

        return creditServiceClient.createCredit(requestBody);
    }
}
