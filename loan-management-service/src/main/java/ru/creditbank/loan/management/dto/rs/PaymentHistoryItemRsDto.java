package ru.creditbank.loan.management.dto.rs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.creditbank.loan.management.enums.PaymentTypeEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record PaymentHistoryItemRsDto(
        UUID paymentId,
        BigDecimal amount,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant datetime,
        PaymentTypeEnum type,
        BigDecimal newBalance
) {
}
