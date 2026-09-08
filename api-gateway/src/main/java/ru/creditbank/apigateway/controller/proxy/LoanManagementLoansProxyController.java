package ru.creditbank.apigateway.controller.proxy;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.common.library.client.LoanManagementLoansServiceClient;
import ru.creditbank.common.library.dto.loan.management.rq.CreateLoanRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.LoanListRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.LoanRsDto;

import java.util.UUID;

@RestController
@RequestMapping("/loan-management-service/api/v1/loans/")
@RequiredArgsConstructor
public class LoanManagementLoansProxyController {

    private final LoanManagementLoansServiceClient loanManagementLoansServiceClient;

    @PostMapping("/create")
    ResponseEntity<LoanRsDto> create(@Valid @RequestBody CreateLoanRqDto rqDto) {
        return loanManagementLoansServiceClient.create(rqDto);
    }

    @GetMapping("/info/{loanId}")
    ResponseEntity<LoanRsDto> info(@PathVariable("loanId") UUID loanId) {
        return loanManagementLoansServiceClient.info(loanId);
    }

    @GetMapping("/list/{userId}")
    ResponseEntity<LoanListRsDto> list(@PathVariable("userId") UUID userId) {
        return loanManagementLoansServiceClient.list(userId);
    }
}
