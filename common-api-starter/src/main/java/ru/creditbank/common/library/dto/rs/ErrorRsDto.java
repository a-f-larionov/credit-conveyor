package ru.creditbank.common.library.dto.rs;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorRsDto(
        Instant timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
}
