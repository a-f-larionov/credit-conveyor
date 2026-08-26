package ru.creditbank.apigateway.feign;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "credit-service",
        url = "${services.credit-service.url}"
)
public interface CreditServiceClient {

    @PostMapping("/credit-service/api/v1/create")
    JsonNode createCredit(@RequestBody JsonNode requestBody);

}