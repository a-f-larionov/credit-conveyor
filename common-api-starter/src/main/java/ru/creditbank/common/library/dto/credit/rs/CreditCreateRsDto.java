package ru.creditbank.common.library.dto.credit.rs;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.creditbank.common.library.enums.CreditStatusEnum;

import java.time.Instant;
import java.util.UUID;

@Builder
public record CreditCreateRsDto(

        UUID id,
        CreditStatusEnum status,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        Instant createdAt) {
}
