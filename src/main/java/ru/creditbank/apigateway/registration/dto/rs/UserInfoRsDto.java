package ru.creditbank.apigateway.registration.dto.rs;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.creditbank.apigateway.registration.dto.AuthDto;
import ru.creditbank.apigateway.registration.dto.FullNameDto;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class UserInfoRsDto extends AuthDto {

    FullNameDto fullNameDto;
}
