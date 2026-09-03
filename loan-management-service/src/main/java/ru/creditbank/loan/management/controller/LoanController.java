package ru.creditbank.loan.management.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.loan.management.dto.rq.CreateLoanRqDto;
import ru.creditbank.loan.management.dto.rs.LoanListRsDto;
import ru.creditbank.loan.management.dto.rs.LoanRsDto;
import ru.creditbank.loan.management.service.LoanService;

import java.util.UUID;

@RestController
@RequestMapping("/loan-management-service/api/v1/loans/")
@RequiredArgsConstructor

public class LoanController {

    private final LoanService loanService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanRsDto create(@Valid @RequestBody CreateLoanRqDto rqDto) {

        return loanService.create(rqDto);
    }

    @GetMapping("/info/{loanId}")
    public LoanRsDto info(@PathVariable UUID loanId) {

        return loanService.info(loanId);
    }

    @GetMapping("/list/{userId}")
    public LoanListRsDto list(@PathVariable UUID userId) {

        return loanService.list(userId);
    }
}
