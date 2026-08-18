package ru.creditbank.apigateway.registration.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.apigateway.jwt.service.JwtService;
import ru.creditbank.apigateway.registration.dto.rs.ErrorRsDto;
import ru.creditbank.apigateway.registration.dto.rs.LoginRsDto;

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
        var userModel = TestFixtures.builUserModel(registerRqDto);

        performPostWithDto("/api/v1/auth/register", registerRqDto, status().isCreated());

        // when
        var rsDto = performPostWithDto("/api/v1/auth/login", loginRqDto, LoginRsDto.class, status().isOk());

        // then
        assertTrue(jwtService.isTokenValid(rsDto.getToken(), userModel));
    }

    @Test
    void testForbiddenRequestNoToken    () throws Exception {
        // given
        var userInfoRqDto = buildUserInfoRqDto("email@email.com");

        // when-then
        var rsDto = performPostWithDto("/api/v1/user/info", userInfoRqDto, ErrorRsDto.class, status().isForbidden());
        assertEquals("Unauthorized", rsDto.getError());
    }
}