package ru.creditbank.apigateway.registration.dto.rq;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.creditbank.apigateway.registration.dto.AuthDto;
import ru.creditbank.apigateway.registration.dto.FullNameDto;

@Data
@SuperBuilder
@NoArgsConstructor
public class RegisterRqDto extends AuthDto {

    @NotNull
    private FullNameDto fullName;

}


