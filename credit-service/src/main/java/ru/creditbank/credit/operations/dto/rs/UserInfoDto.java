package ru.creditbank.credit.operations.dto.rs;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserInfoDto(
        UUID userId,
        String fullName,
        String email
) {

}
