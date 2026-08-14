package ru.creditbank.apigateway.registration.rest;

import org.junit.jupiter.api.Test;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegisterControllerTest extends SpringBootMvcBaseTest {

    @Test
    public void testRegisterSuccess() throws Exception {
        // given
        var rqDto = TestFixtures.buildRegisterRqDto();

        // when-then
        performPostWithDto("/api/v1/auth/register", rqDto, status().isCreated());
    }

    @Test
    public void testRegisterInvalidData() throws Exception {
        // given
        var rqDto = TestFixtures.buildRegisterRqDto();
        rqDto.setPassword("abc"); // invalid email

        // when-then
        performPostWithDto("/api/v1/auth/register", rqDto, status().isBadRequest());
    }

    @Test
    public void testRegisterConflict() throws Exception {
        // given
        var rqDto = TestFixtures.buildRegisterRqDto();

        // when
        performPostWithDto("/api/v1/auth/register", rqDto, status().isCreated());

        // then
        performPostWithDto("/api/v1/auth/register", rqDto, status().isConflict());
    }
}
