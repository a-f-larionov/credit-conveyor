package ru.creditbank.common.library.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.creditbank.common.library.dto.loan.management.rq.PaymentRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.UserLoanPaymentsStatisticRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.PaymentHistoryRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.PaymentRsDto;

import java.util.UUID;

@FeignClient(
        name = "loan-management-service/payments",
        url = "${services.loan-management-service.url}",
        path = "/loan-management-service/api/v1/payments/"
)
public interface LoanManagementPaymentsServiceClient {

    @PostMapping("/process")
    ResponseEntity<PaymentRsDto> create(@Valid @RequestBody PaymentRqDto rqDto);

    @GetMapping("/history/{loanId}")
    ResponseEntity<PaymentHistoryRsDto> history(@PathVariable("loanId") UUID loanId);

    @GetMapping("/user-statistic/{userId}")
    UserLoanPaymentsStatisticRsDto userStatistic(@PathVariable("userId") UUID userId);
}
