package ru.creditbank.loan.management.dto.rs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.creditbank.loan.management.enums.PaymentStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record LoanPaymentScheduleRsDto(
        UUID id,
        UUID loanId,

        Integer number,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant date,
        BigDecimal interestAmount,
        BigDecimal principalAmount,
        BigDecimal remainAmount,
        PaymentStatusEnum status
) {
}
