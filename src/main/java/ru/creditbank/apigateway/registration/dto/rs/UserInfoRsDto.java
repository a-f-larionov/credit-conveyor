package ru.creditbank.apigateway.registration.dto.rs;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.creditbank.apigateway.registration.dto.AuthDto;
import ru.creditbank.apigateway.registration.dto.FullNameDto;

@Data
@SuperBuilder
@NoArgsConstructor
public class UserInfoRsDto extends AuthDto {

    private FullNameDto fullNameDto;
}
