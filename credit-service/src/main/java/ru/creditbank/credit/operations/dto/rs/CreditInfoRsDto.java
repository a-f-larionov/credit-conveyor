package ru.creditbank.credit.operations.dto.rs;

import lombok.Builder;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Builder
public record CreditInfoRsDto(

        UUID id,
        UUID userId,
        String userFullName,
        BigDecimal requestedAmount,
        Integer termMonths,
        CreditStatusEnum status,
        Date creationDate,
        Date lastUpdated
) {
}
