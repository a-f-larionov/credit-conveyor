package ru.creditbank.loan.management.dto.rs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.creditbank.loan.management.enums.LoanStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record LoanRsDto(
        UUID loanId,
        UUID userId,
        BigDecimal totalAmount,
        BigDecimal remainingAmount,
        Integer termMonths,
        BigDecimal interestRate,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant nextPaymentDate,
        LoanStatusEnum status
) {
}
