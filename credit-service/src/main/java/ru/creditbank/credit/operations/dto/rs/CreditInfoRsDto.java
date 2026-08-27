package ru.creditbank.credit.operations.dto.rs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CreditInfoRsDto(

        UUID id,
        UserInfoDto userInfo,
        LoadDetailsDto loanDetails,
        CreditStatusEnum status,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant createdAt
) {
}
