package ru.creditbank.apigateway.controller.proxy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.apigateway.feign.LoanManagementPaymentsServiceClient;
import ru.creditbank.common.library.dto.loan.management.rq.PaymentRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.PaymentHistoryRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.PaymentRsDto;

import java.util.UUID;

@RestController
@RequestMapping("/laon-maanagement-service/api/v1/payments/")
@RequiredArgsConstructor
@Slf4j
public class LoanManagementPaymentsProxyController {

    private final LoanManagementPaymentsServiceClient loanManagementPaymentsServiceClient;

    @PostMapping("/process")
    ResponseEntity<PaymentRsDto> create(@Valid @RequestBody PaymentRqDto paymentRqDto) {
        return loanManagementPaymentsServiceClient.create(paymentRqDto);
    }

    @GetMapping("/history/{loanId}")
    ResponseEntity<PaymentHistoryRsDto> history(@PathVariable UUID loanId) {
        return loanManagementPaymentsServiceClient.history(loanId);
    }
}
