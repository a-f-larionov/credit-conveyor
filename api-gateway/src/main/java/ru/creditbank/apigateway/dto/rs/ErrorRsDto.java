package ru.creditbank.apigateway.dto.rs;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.jackson.Jacksonized;


@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
@Builder
@Jacksonized
public class ErrorRsDto {

    String error;
}
