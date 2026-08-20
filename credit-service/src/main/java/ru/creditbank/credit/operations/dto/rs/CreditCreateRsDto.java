package ru.creditbank.credit.operations.dto.rs;


import lombok.Builder;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;

import java.util.Date;
import java.util.UUID;

@Builder
public record CreditCreateRsDto(UUID id, CreditStatusEnum status, Date createAt) {
}
