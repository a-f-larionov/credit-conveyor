package ru.creditbank.apigateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.common.library.dto.rs.ErrorRsDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.apigateway.TestFixtures.buildRegisterRqDto;
import static ru.creditbank.apigateway.TestFixtures.buildUserInfoRqDto;

public class UserControllerTest extends SpringBootMvcBaseTest {

    @Test
    void testUserInfo() {
        // given
        var registerRqDto = buildRegisterRqDto();
        var loginRqDto = TestFixtures.buildLoginRqDto(registerRqDto);

        performPostWithDto("/api/v1/auth/register", registerRqDto, status().isCreated());
        var rsDto = performPostWithDto("/api/v1/auth/login", loginRqDto, LoginRsDto.class, status().isOk());
        var token = rsDto.token();

        // when
        var userInfoRsDto = performPostWithDto("/api/v1/user/info", loginRqDto, UserInfoRsDto.class, status().isOk(), token);

        // then
        assertEquals(registerRqDto.email(), userInfoRsDto.email());

        var rqFNDto = registerRqDto.fullName();
        var rsFNDto = userInfoRsDto.fullName();
        assertEquals(rqFNDto.firstName(), rsFNDto.firstName());
        assertEquals(rqFNDto.lastName(), rsFNDto.lastName());
        assertEquals(rqFNDto.middleName(), rsFNDto.middleName());
    }

    @Test
    void testUserIsUnAuthorized() {
        // given
        var token = "invalid-token";
        var userInfoRqDto = buildUserInfoRqDto("user@user.ru");

        // when-then
        var rsDto = performPostWithDto("/api/v1/user/info", userInfoRqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    @Sql(scripts = "/sql/create-admin-user.sql", config = @SqlConfig(separator = ";;"))
    void testUserInfoAdmin() {
        // given
        var targetUserRegisterRqDto = buildRegisterRqDto("targetUser@server.org", "Password123");
        performPostWithDto("/api/v1/auth/register", targetUserRegisterRqDto, status().isCreated());
        var targetUserInfoRqDTO = buildUserInfoRqDto(targetUserRegisterRqDto.email());

        var adminLoginRqDto = TestFixtures.buildLoginRqDto("admin@admin.ru", "Admin123");
        var rsDto = performPostWithDto("/api/v1/auth/login", adminLoginRqDto, LoginRsDto.class, status().isOk());
        var token = rsDto.token();

        // when
        var userInfoRsDto = performPostWithDto("/api/v1/user/info", targetUserInfoRqDTO, UserInfoRsDto.class, status().isOk(), token);

        // then
        assertEquals(targetUserInfoRqDTO.email(), userInfoRsDto.email());

        var rsFNDto = userInfoRsDto.fullName();
        var targetFNDto = targetUserRegisterRqDto.fullName();
        assertEquals(targetFNDto.firstName(), rsFNDto.firstName());
        assertEquals(targetFNDto.middleName(), rsFNDto.middleName());
        assertEquals(targetFNDto.lastName(), rsFNDto.lastName());
    }

    @Test
    void testUserInfoAdminForbidden() {
        // given
        var registerUser1RqDto = buildRegisterRqDto("user1@server.org", "Password1");
        var loginUser1RqDto = TestFixtures.buildLoginRqDto(registerUser1RqDto);
        var userInfo2RqDto = buildUserInfoRqDto("user2@server.org");

        performPostWithDto("/api/v1/auth/register", registerUser1RqDto, status().isCreated());
        var rsDto = performPostWithDto("/api/v1/auth/login", loginUser1RqDto, LoginRsDto.class, status().isOk());
        var token = rsDto.token();

        // when
        var errorRsDto = performPostWithDto("/api/v1/user/info", userInfo2RqDto, ErrorRsDto.class, status().isForbidden(), token);

        // then
        assertEquals("Forbidden", errorRsDto.message());
    }
}
