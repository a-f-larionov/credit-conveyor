package ru.creditbank.apigateway.registration.rest;

import org.junit.jupiter.api.Test;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.apigateway.registration.dto.LoginRqDto;
import ru.creditbank.apigateway.registration.dto.LoginRsDto;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends SpringBootMvcBaseTest {

    @Test
    void testLogin() throws Exception {
        // given
        var registerRqDto = TestFixtures.builRegisterRqDto();
        var loginRqDTO = LoginRqDto.builder()
                .email(registerRqDto.getEmail())
                .password(registerRqDto.getPassword())
                .build();

        performPostWithDto("/api/v1/auth/register", registerRqDto, status().isCreated());
        // when

        var rsDto = performPostWithDto("/api/v1/auth/login", loginRqDTO, LoginRsDto.class, status().isOk());

        System.out.println(rsDto);
        // then
    }
}