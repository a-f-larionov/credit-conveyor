package ru.creditbank.credit.operations.dto.rs;

import lombok.Builder;

@Builder
public record ErrorRsDto(String error) {
}
