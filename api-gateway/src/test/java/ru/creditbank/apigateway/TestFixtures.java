package ru.creditbank.apigateway;

import ru.creditbank.apigateway.dto.FullNameDto;
import ru.creditbank.apigateway.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;

public class TestFixtures {


    public static RegisterRqDto buildRegisterRqDto() {
        var unique = 1L;
        return buildRegisterRqDto(unique, "Password" + unique);
    }

    public static RegisterRqDto buildRegisterRqDto(String password) {
        var unique = 1L;
        return buildRegisterRqDto(unique, password);
    }

    public static RegisterRqDto buildRegisterRqDto(Long unique, String password) {
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
                .email(registerRqDto.email())
                .password(registerRqDto.password())
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

    public static UserInfoRsDto buildRsDto() {
        return UserInfoRsDto.builder()
                .email("email@mail.ru")
                .build();
    }
}
