package ru.creditbank.apigateway.dto.rq;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;
import ru.creditbank.apigateway.dto.AuthDto;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Jacksonized
public class LoginRqDto extends AuthDto {

}