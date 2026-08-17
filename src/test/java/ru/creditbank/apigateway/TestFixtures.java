package ru.creditbank.apigateway;

import ru.creditbank.apigateway.core.UserModel;
import ru.creditbank.apigateway.registration.dto.FullNameDto;
import ru.creditbank.apigateway.registration.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.registration.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.registration.dto.rq.UserInfoRqDto;

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

    public static UserModel builUserModel(RegisterRqDto rqDto) {
        return UserModel.builder()
                .email(rqDto.getEmail())
                .passwordHash(rqDto.getPassword())
                .firstName(rqDto.getFullName().getFirstName())
                .middleName(rqDto.getFullName().getMiddleName())
                .lastName(rqDto.getFullName().getLastName())
                .build();
    }

    public static LoginRqDto buildLoginRqDto(String email, String password) {
        return LoginRqDto.builder()
                .email(email)
                .password(password)
                .build();
    }
}
