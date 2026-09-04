package ru.creditbank.common.library.dto.credit.rq;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.creditbank.common.library.enums.CreditStatusEnum;

@Builder
public record StatusUpdateRqDto(

        @NotNull
        CreditStatusEnum status,

        @Size(max = 500)
        String managerComment
) {
}
