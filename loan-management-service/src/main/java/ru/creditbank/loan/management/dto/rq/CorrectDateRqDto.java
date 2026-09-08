package ru.creditbank.loan.management.dto.rq;

import lombok.Builder;

import java.time.Instant;

@Builder
public record CorrectDateRqDto(
        Instant newDate
) {
}
