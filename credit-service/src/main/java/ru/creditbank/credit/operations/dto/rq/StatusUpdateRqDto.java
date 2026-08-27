package ru.creditbank.credit.operations.dto.rq;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;

@Builder
public record StatusUpdateRqDto(

        @NotNull
        CreditStatusEnum status,

        @Size(max = 500)
        String managerComment
) {
}
