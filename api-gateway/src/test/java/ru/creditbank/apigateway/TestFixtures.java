package ru.creditbank.apigateway;

import ru.creditbank.apigateway.dto.FullNameDto;
import ru.creditbank.apigateway.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.dto.rq.UserInfoRqDto;

public class TestFixtures {


    public static RegisterRqDto buildRegisterRqDto() {
        var unique = 1;
        return buildRegisterRqDto(unique, "Password" + unique);
    }

    public static RegisterRqDto buildRegisterRqDto(int unique, String password) {
        return RegisterRqDto.builder()
                .fullName(FullNameDto.builder()
                        .firstName("firstName-" + unique)
                        .middleName("middleName-" + unique)
                        .lastName("lastName-" + unique)
                        .build())
                .email("email_" + unique + "@mail.com")
                .password(password)
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

    public static LoginRqDto buildLoginRqDto(String email, String password) {
        return LoginRqDto.builder()
                .email(email)
                .password(password)
                .build();
    }
}
