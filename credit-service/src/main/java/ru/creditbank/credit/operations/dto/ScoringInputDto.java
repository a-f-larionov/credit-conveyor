package ru.creditbank.credit.operations.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record ScoringInputDto(
        UUID userId,
        UUID creditId,
        Integer employmentMonths,
        Integer termMonths,
        BigDecimal requestedAmount,
        BigDecimal monthlyIncome,
        Integer allDonePayments,
        Integer allOverduePayments
) {
}
