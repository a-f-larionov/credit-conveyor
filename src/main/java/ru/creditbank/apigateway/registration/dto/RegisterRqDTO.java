package ru.creditbank.apigateway.registration.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisterRqDTO extends AuthDTO{

    @NotNull
    private FullNameRqDTO fullName;

    @Getter
    @Builder
    public static class FullNameRqDTO {

        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        private String firstName;

        @NotBlank
        @Size(min = 2, max = 50)
        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        private String lastName;

        @Pattern(regexp = "^[А-Яа-яЁёA-Za-z-]+$")
        @Size(min = 2,max = 50)
        private String middleName;
    }
}


