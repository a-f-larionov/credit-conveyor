package ru.creditbank.apigateway.registration.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FullNameDto {

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
    private String lastName;

    @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
    @Size(min = 2, max = 50)
    private String middleName;
}
