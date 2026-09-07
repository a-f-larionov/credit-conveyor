package ru.creditbank.common.library.dto.loan.management.rs;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record PaymentRsDto(
        UUID paymentId,
        BigDecimal principalRemainingAmount,
        @Nullable
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant nextPaymentDate
) {
}
