package ru.creditbank.loan.management.dto.rq;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import ru.creditbank.loan.management.enums.PaymentTypeEnum;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PaymentRqDto(
        @NotNull
        UUID loanId,

        @NotNull
        @Positive
        @Digits(integer = 13, fraction = 2)
        BigDecimal amount,

        @NotNull
        PaymentTypeEnum type) {
}
