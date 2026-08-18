package ru.creditbank.apigateway.registration.dto.rs;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;


@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class LoginRsDto {
    private final String token;
}
