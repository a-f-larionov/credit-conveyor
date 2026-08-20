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
        var token = rsDto.getToken();

        // when
        var userInfoRsDto = performPostWithDto("/api/v1/user/info", loginRqDto, UserInfoRsDto.class, status().isOk(), token);

        // then
        assertEquals(registerRqDto.getEmail(), userInfoRsDto.getEmail());

        var rqFNDto = registerRqDto.getFullName();
        var rsFNDto = userInfoRsDto.getFullNameDto();
        assertEquals(rqFNDto.getFirstName(), rsFNDto.getFirstName());
        assertEquals(rqFNDto.getLastName(), rsFNDto.getLastName());
        assertEquals(rqFNDto.getMiddleName(), rsFNDto.getMiddleName());
    }

    @Test
    @Sql(scripts = "/sql/create-admin-user.sql", config = @SqlConfig(separator = ";;"))
    void testUserInfoAdmin() throws Exception {
        // given

        var loginRqDTO = TestFixtures.buildLoginRqDto("admin@admin.ru", "Admin123");
        var rsDto = performPostWithDto("/api/v1/auth/login", loginRqDTO, LoginRsDto.class, status().isOk());
        var token = rsDto.getToken();

        // when
        var userInfoRsDto = performPostWithDto("/api/v1/user/info-admin", loginRqDTO, UserInfoRsDto.class, status().isOk(), token);

        // then
        assertEquals(loginRqDTO.getEmail(), userInfoRsDto.getEmail());

        var rsFNDto = userInfoRsDto.getFullNameDto();
        assertNull(rsFNDto.getFirstName());
        assertNull(rsFNDto.getLastName());
        assertNull(rsFNDto.getMiddleName());
    }

    @Test
    void testUserInfoAdminForbidden() throws Exception {
        // given
        var registerRqDto = TestFixtures.buildRegisterRqDto();
        var loginRqDTO = TestFixtures.buildLoginRqDto(registerRqDto);

        performPostWithDto("/api/v1/auth/register", registerRqDto, status().isCreated());
        var rsDto = performPostWithDto("/api/v1/auth/login", loginRqDTO, LoginRsDto.class, status().isOk());
        var token = rsDto.getToken();

        // when
        var errorRsDto = performPostWithDto("/api/v1/user/info-admin", loginRqDTO, ErrorRsDto.class, status().isForbidden(), token);

        assertEquals("Forbidden", errorRsDto.getError());
    }
}
