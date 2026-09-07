package ru.creditbank.apigateway.feign;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.creditbank.common.library.dto.loan.management.rq.CreateLoanRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.LoanListRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.LoanRsDto;

import java.util.UUID;

@FeignClient(
        name = "loan-management-service",
        url = "${services.loan-management-service.url}",
        path = "/loan-management-service/api/v1/loans"
)
public interface LoanManagementLoansServiceClient {

    @PostMapping("/create")
    ResponseEntity<LoanRsDto> create(@Valid @RequestBody CreateLoanRqDto rqDto);

    @GetMapping("/info/{loanId}")
    ResponseEntity<LoanRsDto> info(@PathVariable UUID loanId);

    @GetMapping("/list/{userId}")
    ResponseEntity<LoanListRsDto> list(@PathVariable UUID userId);
}