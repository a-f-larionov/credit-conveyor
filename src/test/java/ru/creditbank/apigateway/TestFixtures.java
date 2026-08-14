package ru.creditbank.apigateway;

import ru.creditbank.apigateway.registration.dto.RegisterRqDto;

public class TestFixtures {

    public static RegisterRqDto builRegisterRqDto() {
        return builRegisterRqDto(1);
    }

    public static RegisterRqDto builRegisterRqDto(int unique) {
        return RegisterRqDto.builder()
                .fullName(RegisterRqDto.FullNameRqDTO.builder()
                        .firstName("firstName-" + unique)
                        .middleName("middleName-" + unique)
                        .lastName("lastName-" + unique)
                        .build())
                .email("email_" + unique + "@mail.com")
                .password("Password" + unique)
                .build();
    }
}
