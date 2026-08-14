package ru.creditbank.apigateway;

import ru.creditbank.apigateway.registration.dto.FullNameDto;
import ru.creditbank.apigateway.registration.dto.LoginRqDto;
import ru.creditbank.apigateway.registration.dto.RegisterRqDto;
import ru.creditbank.apigateway.registration.dto.UserInfoRqDto;

public class TestFixtures {

    public static RegisterRqDto buildRegisterRqDto() {
        return buildRegisterRqDto(1);
    }

    public static RegisterRqDto buildRegisterRqDto(int unique) {
        return RegisterRqDto.builder()
                .fullName(FullNameDto.builder()
                        .firstName("firstName-" + unique)
                        .middleName("middleName-" + unique)
                        .lastName("lastName-" + unique)
                        .build())
                .email("email_" + unique + "@mail.com")
                .password("Password" + unique)
                .build();
    }

    public static LoginRqDto buildLoginRqDto(RegisterRqDto registerRqDto) {
        return LoginRqDto.builder()
                .email(registerRqDto.getEmail())
                .password(registerRqDto.getPassword())
                .build();
    }

    public static UserInfoRqDto buildUserInfoRqDto(String email) {
        return UserInfoRqDto.builder()
                .email(email)
                .build();
    }
}
