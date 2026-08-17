package ru.creditbank.apigateway.registration.dto.rq;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.creditbank.apigateway.registration.dto.AuthDto;

@Data
@SuperBuilder
@NoArgsConstructor
public class LoginRqDto extends AuthDto {

}