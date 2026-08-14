package ru.creditbank.apigateway;

import ru.creditbank.apigateway.registration.dto.RegisterRqDTO;

public class TestFixtures {

    public static RegisterRqDTO builRegisterRqDto() {
        return builRegisterRqDto(1);
    }

    public static RegisterRqDTO builRegisterRqDto(int unique) {
        return RegisterRqDTO.builder()
                .fullName(RegisterRqDTO.FullNameRqDTO.builder()
                        .firstName("firstName-" + unique)
                        .middleName("middleName-" + unique)
                        .lastName("lastName-" + unique)
                        .build())
                .email("email_" + unique + "@mail.com")
                .password("Password" + unique)
                .build();
    }
}
