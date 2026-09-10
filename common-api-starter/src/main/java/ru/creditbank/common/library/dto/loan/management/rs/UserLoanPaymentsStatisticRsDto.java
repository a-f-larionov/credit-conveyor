package ru.creditbank.common.library.dto.loan.management.rs;

import lombok.Builder;

@Builder
public record UserLoanPaymentsStatisticRsDto(
        Integer allDonePayments,
        Integer allOverduePayments
) {
}
