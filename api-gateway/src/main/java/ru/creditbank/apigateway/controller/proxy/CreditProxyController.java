package ru.creditbank.apigateway.controller.proxy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.common.library.client.CreditServiceClient;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;
import ru.creditbank.common.library.dto.credit.rs.StatusUpdateRsDto;

import java.util.UUID;

@RestController
@RequestMapping("/credit-service/api/v1/credits")
@RequiredArgsConstructor
public class CreditProxyController {

    private final CreditServiceClient creditServiceClient;

    @PostMapping("/create")
    public ResponseEntity<CreditCreateRsDto> createCredit(@Valid @RequestBody CreditCreateRqDto creditCreateRqDto) {
        return creditServiceClient.createCredit(creditCreateRqDto);
    }

    @PatchMapping("/status/update/{creditId}")
    public ResponseEntity<StatusUpdateRsDto> statusUpdate(
            @Valid @RequestBody StatusUpdateRqDto statusUpdateRqDto,
            @PathVariable(name = "creditId") UUID creditId) {
        return creditServiceClient.statusUpdate(statusUpdateRqDto, creditId);
    }
}
