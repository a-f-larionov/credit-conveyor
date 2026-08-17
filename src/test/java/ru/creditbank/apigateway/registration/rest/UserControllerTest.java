package ru.creditbank.apigateway.registration.rest;

import org.junit.jupiter.api.Test;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.apigateway.registration.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.registration.dto.rs.UserInfoRsDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends SpringBootMvcBaseTest {

    @Test
    void testAuthByToken() throws Exception {
        // given
        var registerRqDto = TestFixtures.buildRegisterRqDto();
        var loginRqDTO = TestFixtures.buildLoginRqDto(registerRqDto);

        performPostWithDto("/api/v1/auth/register", registerRqDto, status().isCreated());

        // when
        var rsDto = performPostWithDto("/api/v1/auth/login", loginRqDTO, LoginRsDto.class, status().isOk());
        var token = rsDto.getToken();

        // then
        var userInfoRsDto = performPostWithDto("/api/v1/user/info", loginRqDTO, UserInfoRsDto.class, status().isOk(), token);

        assertEquals(registerRqDto.getEmail(), userInfoRsDto.getEmail());

        var rqFNDto = registerRqDto.getFullName();
        var rsFNDto = userInfoRsDto.getFullNameDto();
        assertEquals(rqFNDto.getFirstName(), rsFNDto.getFirstName());
        assertEquals(rqFNDto.getLastName(), rsFNDto.getLastName());
        assertEquals(rqFNDto.getMiddleName(), rsFNDto.getMiddleName());
    }
}
