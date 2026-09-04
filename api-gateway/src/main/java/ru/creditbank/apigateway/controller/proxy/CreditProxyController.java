package ru.creditbank.apigateway.controller.proxy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.apigateway.feign.CreditServiceClient;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;

import java.util.UUID;

@RestController
@RequestMapping("/credit-service/api/v1/")
@RequiredArgsConstructor
@Slf4j
public class CreditProxyController {

    private final CreditServiceClient creditServiceClient;

    @PostMapping("/create")
    public ResponseEntity<CreditCreateRsDto> createCredit(@Valid @RequestBody CreditCreateRqDto rqDto) {
        log.info("Proxy credit: {}", rqDto.fullName());
        return creditServiceClient.createCredit(rqDto);
    }

    @PatchMapping("/status/update/{creditId}")
    public ResponseEntity<CreditCreateRsDto> statusUpdate(@Valid @RequestBody StatusUpdateRqDto statusUpdateRqDto, @PathVariable UUID creditId) {
        log.info("Proxy status update for creditId: {}", creditId);
        return creditServiceClient.statusUpdate(statusUpdateRqDto, creditId);
    }
}
