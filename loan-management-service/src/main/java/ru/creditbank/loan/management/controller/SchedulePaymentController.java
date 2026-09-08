package ru.creditbank.loan.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.loan.management.dto.rq.CorrectDateRqDto;
import ru.creditbank.loan.management.dto.rs.LoanPaymentScheduleRsDto;
import ru.creditbank.loan.management.dto.rs.LoanPaymentsScheduleListRsDto;
import ru.creditbank.loan.management.service.SchedulePaymentService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan-management-service/api/v1/schedule-payment/")
@Slf4j
public class SchedulePaymentController {

    private final SchedulePaymentService schedulePaymentService;

    @GetMapping("/list/{loanId}")
    public LoanPaymentsScheduleListRsDto list(@PathVariable("loanId") UUID loanId) {

        return schedulePaymentService.list(loanId);
    }

    @PatchMapping("/correct-date/{paymentId}")
    public LoanPaymentScheduleRsDto correctDate(
            @PathVariable("paymentId") UUID paymentId,
            @Valid @RequestBody CorrectDateRqDto rqDto) {

        return schedulePaymentService.correctDate(paymentId, rqDto);
    }
}
