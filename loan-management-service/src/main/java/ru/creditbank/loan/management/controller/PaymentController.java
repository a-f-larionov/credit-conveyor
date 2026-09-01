package ru.creditbank.loan.management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.loan.management.dto.rq.PaymentRqDto;
import ru.creditbank.loan.management.dto.rs.PaymentHistoryRsDto;
import ru.creditbank.loan.management.dto.rs.PaymentRsDto;
import ru.creditbank.loan.management.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/loan-management-service/api/v1/payments/")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/make")
    public PaymentRsDto makePayment(PaymentRqDto rqDto) {

        return paymentService.makePayment(rqDto);
    }

    @GetMapping("/history/{userId}")
    public PaymentHistoryRsDto history(@PathVariable UUID userId) {

        return paymentService.history(userId);
    }

}
