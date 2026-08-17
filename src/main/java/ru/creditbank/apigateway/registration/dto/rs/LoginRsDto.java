package ru.creditbank.apigateway.registration.dto.rs;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class LoginRsDto {
    private final String token;
}
