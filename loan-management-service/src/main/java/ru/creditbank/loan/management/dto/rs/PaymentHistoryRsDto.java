package ru.creditbank.loan.management.dto.rs;

import lombok.Builder;

import java.util.List;

@Builder
public record PaymentHistoryRsDto(
        List<PaymentHistoryItemRsDto> payments
) {
}
