package ru.creditbank.loan.management.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public LoanPaymentsScheduleListRsDto list(@PathVariable UUID loanId) {

        return schedulePaymentService.list(loanId);
    }
}
