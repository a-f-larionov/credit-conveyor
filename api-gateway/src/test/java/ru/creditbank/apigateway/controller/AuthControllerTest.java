package ru.creditbank.apigateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;
import ru.creditbank.common.library.dto.common.rs.ErrorRsDto;
import ru.creditbank.common.library.service.JwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.apigateway.TestFixtures.*;

class AuthControllerTest extends SpringBootMvcBaseTest {

    @Autowired
    JwtService jwtService;

    @Test
    void testUserDoesNotExists() {
        // given
        var loginRqDto = TestFixtures.buildLoginRqDto("not-exists@mail.ru", "Password123");

        // when
        var rsDto = performPost("/api/v1/auth/login", loginRqDto, ErrorRsDto.class, status().isNotFound());

        // then
        assertEquals("User does not exists", rsDto.message());
    }

    @Test
    void testLoginWithValidToken() {
        // given
        var registerRqDto = buildRegisterRqDto();
        var loginRqDto = buildLoginRqDto(registerRqDto);

        performPost("/api/v1/auth/register", registerRqDto, status().isCreated());

        // when
        var rsDto = performPost("/api/v1/auth/login", loginRqDto, LoginRsDto.class, status().isOk());

        // then
        assertTrue(jwtService.isTokenValid(rsDto.token()));
    }

    @Test
    void testUnauthorizedRequestNoToken() {
        // given
        var userInfoRqDto = buildUserInfoRqDto("email@email.com");

        // when
        var rsDto = performPost("/api/v1/user/info", userInfoRqDto, ErrorRsDto.class, status().isUnauthorized());

        // then
        assertEquals("Token is empty", rsDto.message());
    }
}