package ru.creditbank.apigateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.apigateway.dto.rs.ErrorRsDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends SpringBootMvcBaseTest {

    @Test
    void testUserInfo() throws Exception {
        // given
        var registerRqDto = TestFixtures.buildRegisterRqDto();
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
    void testUserIsUnAuthorized() throws Exception {
        // given
        var token = "invalid-token";
        var userInfoRqDto = TestFixtures.buildUserInfoRqDto("user@user.ru");

        // when-then
        var rsDto = performPostWithDto("/api/v1/user/info", userInfoRqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        assertEquals("Token Invalid", rsDto.error());
    }

    @Test
    @Sql(scripts = "/sql/create-admin-user.sql", config = @SqlConfig(separator = ";;"))
    void testUserInfoAdmin() throws Exception {
        // given
        var loginRqDTO = TestFixtures.buildLoginRqDto("admin@admin.ru", "Admin123");
        var rsDto = performPostWithDto("/api/v1/auth/login", loginRqDTO, LoginRsDto.class, status().isOk());
        var token = rsDto.token();

        // when
        var userInfoRsDto = performPostWithDto("/api/v1/user/info-admin", loginRqDTO, UserInfoRsDto.class, status().isOk(), token);

        // then
        assertEquals(loginRqDTO.email(), userInfoRsDto.email());

        var rsFNDto = userInfoRsDto.fullName();
        assertNull(rsFNDto.firstName());
        assertNull(rsFNDto.lastName());
        assertNull(rsFNDto.middleName());
    }

    @Test
    void testUserInfoAdminForbidden() throws Exception {
        // given
        var registerRqDto = TestFixtures.buildRegisterRqDto();
        var loginRqDTO = TestFixtures.buildLoginRqDto(registerRqDto);

        performPostWithDto("/api/v1/auth/register", registerRqDto, status().isCreated());
        var rsDto = performPostWithDto("/api/v1/auth/login", loginRqDTO, LoginRsDto.class, status().isOk());
        var token = rsDto.token();

        // when
        var errorRsDto = performPostWithDto("/api/v1/user/info-admin", loginRqDTO, ErrorRsDto.class, status().isForbidden(), token);

        // then
        assertEquals("Forbidden", errorRsDto.error());
    }
}
