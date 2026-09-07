package ru.creditbank.common.library.dto.loan.management.rs;

import lombok.Builder;

import java.util.List;

@Builder
public record PaymentHistoryRsDto(
        List<PaymentHistoryItemRsDto> payments
) {
}
