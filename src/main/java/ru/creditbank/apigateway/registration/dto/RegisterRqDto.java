package ru.creditbank.apigateway.registration.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisterRqDto extends AuthDto {

    @NotNull
    private FullNameDto fullName;
}


