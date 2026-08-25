package ru.creditbank.apigateway.controller;

import org.junit.jupiter.api.Test;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegisterControllerTest extends SpringBootMvcBaseTest {

    @Test
    public void testRegisterSuccess() {
        // given
        var rqDto = TestFixtures.buildRegisterRqDto();

        // when-then
        performPostWithDto("/api/v1/auth/register", rqDto, status().isCreated());
    }

    @Test
    public void testRegisterInvalidData() {
        // given
        var rqDto = TestFixtures.buildRegisterRqDto("no_upper_case_password");

        // when-then
        performPostWithDto("/api/v1/auth/register", rqDto, status().isBadRequest());
    }

    @Test
    public void testRegisterConflict() {
        // given
        var rqDto = TestFixtures.buildRegisterRqDto();

        // when
        performPostWithDto("/api/v1/auth/register", rqDto, status().isCreated());

        // then
        performPostWithDto("/api/v1/auth/register", rqDto, status().isConflict());
    }
}
