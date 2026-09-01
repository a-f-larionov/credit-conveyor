package ru.creditbank.loan.management.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.loan.management.dto.rq.CreateLoanRqDto;
import ru.creditbank.loan.management.dto.rs.CreateLoanRsDto;
import ru.creditbank.loan.management.dto.rs.LoanListRsDto;
import ru.creditbank.loan.management.service.LoanService;

@RestController
@RequestMapping("/loan-management-service/api/v1/loans/")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/create")
    public CreateLoanRsDto createLoan(CreateLoanRqDto rqDto) {

        return loanService.createLoan(rqDto);
    }

    @GetMapping("/list/{userId}")
    public LoanListRsDto list(@PathVariable String userId) {

        return loanService.list(userId);
    }
}
