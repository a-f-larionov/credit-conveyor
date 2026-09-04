package ru.creditbank.apigateway.feign;


import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;

import java.util.UUID;

@FeignClient(
        name = "credit-service",
        url = "${services.credit-service.url}"
)
public interface CreditServiceClient {

    @PostMapping("/credit-service/api/v1/create")
    ResponseEntity<CreditCreateRsDto> createCredit(@Valid @RequestBody CreditCreateRqDto rqDto);

    @PatchMapping("/credit-service/api/v1/status/update/{creditId}")
    ResponseEntity<CreditCreateRsDto> statusUpdate(@Valid @RequestBody StatusUpdateRqDto rqDto, @PathVariable UUID creditId);
}