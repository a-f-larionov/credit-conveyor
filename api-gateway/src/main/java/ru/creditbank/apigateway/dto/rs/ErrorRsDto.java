package ru.creditbank.apigateway.dto.rs;

import lombok.Builder;

@Builder
public record ErrorRsDto(String message) {
}
