package ru.creditbank.apigateway.dto.rq;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import ru.creditbank.apigateway.dto.FullNameDto;

@Builder
public record RegisterRqDto(

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, max = 64)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")
        String password,

        @NotNull
        @Valid
        FullNameDto fullName
) {
}


