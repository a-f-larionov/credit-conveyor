package ru.creditbank.loan.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.common.library.dto.loan.management.rq.PaymentRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.UserLoanPaymentsStatisticRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.PaymentHistoryRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.PaymentRsDto;
import ru.creditbank.loan.management.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/loan-management-service/api/v1/payments/")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public PaymentRsDto processPayment(@Valid @RequestBody PaymentRqDto rqDto) {

        return paymentService.processPayment(rqDto);
    }

    @GetMapping("/history/{loanId}")
    public PaymentHistoryRsDto history(@PathVariable("loanId") UUID loanId) {

        return paymentService.history(loanId);
    }

    @GetMapping("/user-statistic/{userId}")
    public UserLoanPaymentsStatisticRsDto userStatistic(@PathVariable("userId") UUID userId) {

        return paymentService.userStatistic(userId);
    }
}
