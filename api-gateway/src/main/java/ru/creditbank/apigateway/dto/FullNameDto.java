package ru.creditbank.apigateway.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode
@SuperBuilder
@Jacksonized
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
