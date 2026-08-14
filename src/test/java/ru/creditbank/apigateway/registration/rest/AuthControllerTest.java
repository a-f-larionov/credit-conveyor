package ru.creditbank.apigateway.registration.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.jwt.service.JwtService;
import ru.creditbank.apigateway.registration.dto.LoginRsDto;

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
        assertTrue(jwtService.isTokenValid(rsDto.getToken(), loginRqDto.getEmail()));
    }

    @Test
    void testForbiddenRequest() throws Exception {
        // given
        var userInfoRqDto = buildUserInfoRqDto("email@email.com");

        // when-then
        performPostWithDto("/api/v1/user/info", userInfoRqDto, LoginRsDto.class, status().isForbidden());
    }
}