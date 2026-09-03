package ru.creditbank.loan.management.dto.rs;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record LoanPaymentsScheduleListRsDto(
        UUID loanId,
        List<LoanPaymentScheduleRsDto> payments
) {
}
