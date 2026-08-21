package ru.creditbank.apigateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record FullNameDto(

        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        String firstName,

        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        String lastName,

        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        @Size(min = 2, max = 50)
        String middleName

) {
}
