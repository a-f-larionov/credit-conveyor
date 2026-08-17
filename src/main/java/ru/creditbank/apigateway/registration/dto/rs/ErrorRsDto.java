package ru.creditbank.apigateway.registration.dto.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class ErrorRsDto {
    private final String error;
}
