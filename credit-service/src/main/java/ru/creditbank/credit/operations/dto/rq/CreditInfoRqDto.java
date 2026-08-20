package ru.creditbank.credit.operations.dto.rq;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreditInfoRqDto(
        @NotNull
        UUID id
) {
}
