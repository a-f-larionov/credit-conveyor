package ru.creditbank.common.library.dto.loan.management.rs;

import lombok.Builder;

@Builder
public record ClientLoanPaymentsStatisticRsDto(
        Integer allDonePayments,
        Integer allOverduePayments
) {
}
