package ru.creditbank.apigateway.registration.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserInfoRsDto extends AuthDto {

    private FullNameDto fullNameDto;
}
