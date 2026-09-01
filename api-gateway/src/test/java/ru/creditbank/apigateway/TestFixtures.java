package ru.creditbank.apigateway;

import ru.creditbank.apigateway.dto.FullNameDto;
import ru.creditbank.apigateway.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;

public class TestFixtures {
    private static Long lastUniqueIndex = 0L;

    public static RegisterRqDto buildRegisterRqDto() {
        lastUniqueIndex++;
        return buildRegisterRqDto(
                "user" + lastUniqueIndex + "@email.ru",
                "Password" + lastUniqueIndex
        );
    }

    public static RegisterRqDto buildRegisterRqDto(String password) {
        lastUniqueIndex++;
        return buildRegisterRqDto(
                "user" + lastUniqueIndex + "@email.ru",
                password);
    }

    public static RegisterRqDto buildRegisterRqDto(String userEmail, String password) {
        return RegisterRqDto.builder()
                .fullName(FullNameDto.builder()
                        .firstName("firstName")
                        .middleName("middleName")
                        .lastName("lastName")
                        .build())
                .email(userEmail)
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
