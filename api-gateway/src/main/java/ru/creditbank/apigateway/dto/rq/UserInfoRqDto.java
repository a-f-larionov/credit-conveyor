package ru.creditbank.apigateway.dto.rq;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserInfoRqDto(

        @NotBlank
        @Email
        String email
) {
}
