package ru.creditbank.common.library.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;
import ru.creditbank.common.library.dto.credit.rs.StatusUpdateRsDto;

import java.util.UUID;

@FeignClient(
        name = "credit-service",
        url = "${services.credit-service.url}",
        path = "/credit-service/api/v1/credits/"
)
public interface CreditServiceClient {

    @PostMapping("/create")
    ResponseEntity<CreditCreateRsDto> createCredit(@Valid @RequestBody CreditCreateRqDto rqDto);

    @PatchMapping("/status/update/{creditId}")
    ResponseEntity<StatusUpdateRsDto> statusUpdate(
            @Valid @RequestBody StatusUpdateRqDto rqDto,
            @PathVariable("creditId") UUID creditId
    );
}