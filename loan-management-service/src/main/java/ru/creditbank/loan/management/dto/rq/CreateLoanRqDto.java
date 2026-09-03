package ru.creditbank.loan.management.dto.rq;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record CreateLoanRqDto(

        @NotNull
        UUID userId,

        @NotNull
        @Positive
        @Digits(integer = 13, fraction = 2)
        BigDecimal totalAmount,

        @NotNull
        @Positive
        @Min(1)
        Integer termMonths,

        @NotNull
        @Positive
        BigDecimal interestRate,

        @NotNull
        @FutureOrPresent
        Instant firstPaymentDate
) {
}
