package ru.creditbank.apigateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.dto.rs.ErrorRsDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.service.JwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.apigateway.TestFixtures.*;

class AuthControllerTest extends SpringBootMvcBaseTest {

    @Autowired
    JwtService jwtService;

    @Test
    void testLoginWithValidToken() throws Exception {
        // given
        var registerRqDto = buildRegisterRqDto();
        var loginRqDto = buildLoginRqDto(registerRqDto);

        performPostWithDto("/api/v1/auth/register", registerRqDto, status().isCreated());

        // when
        var rsDto = performPostWithDto("/api/v1/auth/login", loginRqDto, LoginRsDto.class, status().isOk());

        // then
        assertTrue(jwtService.isTokenValid(rsDto.token()));
    }

    @Test
    void testForbiddenRequestNoToken() throws Exception {
        // given
        var userInfoRqDto = buildUserInfoRqDto("email@email.com");

        // when
        var rsDto = performPostWithDto("/api/v1/user/info", userInfoRqDto, ErrorRsDto.class, status().isUnauthorized());

        // then
        assertEquals("Unauthorized", rsDto.error());
    }
}