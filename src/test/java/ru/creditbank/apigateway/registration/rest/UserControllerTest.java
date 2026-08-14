package ru.creditbank.apigateway.registration.rest;

import org.junit.jupiter.api.Test;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.apigateway.registration.dto.LoginRsDto;
import ru.creditbank.apigateway.registration.dto.UserInfoRsDto;

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

        var fullNameDto = registerRqDto.getFullName();
        assertEquals(fullNameDto.getFirstName(), fullNameDto.getFirstName());
        assertEquals(fullNameDto.getLastName(), fullNameDto.getLastName());
        assertEquals(fullNameDto.getMiddleName(), fullNameDto.getMiddleName());
    }
}
