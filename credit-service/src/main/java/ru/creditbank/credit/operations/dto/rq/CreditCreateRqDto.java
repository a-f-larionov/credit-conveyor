package ru.creditbank.credit.operations.dto.rq;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CreditCreateRqDto(

        @NotBlank
        @Size(min = 5, max = 100)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z\\s-]+$")
        String fullName,

        @NotNull
        @Positive
        @Digits(integer = 15, fraction = 2)
        BigDecimal requestedAmount,

        @NotNull
        @Min(1)
        Integer termMonths
) {
}
