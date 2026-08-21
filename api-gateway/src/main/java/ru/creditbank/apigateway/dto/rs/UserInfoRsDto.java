package ru.creditbank.apigateway.dto.rs;

import lombok.Builder;
import ru.creditbank.apigateway.dto.FullNameDto;

@Builder
public record UserInfoRsDto(
        String email,
        String password,
        FullNameDto fullName
) {
}
