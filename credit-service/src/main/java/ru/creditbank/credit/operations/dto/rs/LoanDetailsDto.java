package ru.creditbank.credit.operations.dto.rs;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record LoanDetailsDto(

        BigDecimal requestedAmount,
        Integer termMonths,
        BigDecimal interestRate
) {
}
