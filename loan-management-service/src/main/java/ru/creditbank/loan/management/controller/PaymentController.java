package ru.creditbank.loan.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.loan.management.dto.rq.PaymentRqDto;
import ru.creditbank.loan.management.dto.rs.PaymentHistoryRsDto;
import ru.creditbank.loan.management.dto.rs.PaymentRsDto;
import ru.creditbank.loan.management.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/loan-management-service/api/v1/payments/")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public PaymentRsDto processPayment(@Valid @RequestBody PaymentRqDto rqDto) {

        return paymentService.processPayment(rqDto);
    }

    @GetMapping("/history/{loanId}")
    public PaymentHistoryRsDto history(@PathVariable UUID loanId) {

        return paymentService.history(loanId);
    }
}
