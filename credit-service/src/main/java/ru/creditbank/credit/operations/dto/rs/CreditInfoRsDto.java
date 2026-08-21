package ru.creditbank.credit.operations.dto.rs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record CreditInfoRsDto(

        UUID id,
        UUID userId,
        String userFullName,
        BigDecimal requestedAmount,
        Integer termMonths,
        CreditStatusEnum status,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant creationDate,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant lastUpdated
) {
}
