package ru.creditbank.apigateway.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.apigateway.dto.rs.ErrorRsDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.apigateway.TestFixtures.buildLoginRqDto;
import static ru.creditbank.apigateway.TestFixtures.buildRegisterRqDto;

class CreditProxyControllerTest extends SpringBootMvcBaseTest {

    @Value("${services.credit-service.url}")
    private String creditServiceUrl;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createInvalidToken() throws Exception {
        // given
        var token = "invalid-token";
        var rqDto = TestFixtures.buildRegisterRqDto();

        // when
        var rsDto = performPostWithDto("/credit-service/api/v1/create", rqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token Invalid", rsDto.getError());
    }

    @Test
    public void create() throws Exception {
        // given
        var registerRqDto = buildRegisterRqDto();
        var loginRqDto = buildLoginRqDto(registerRqDto);
        performPostWithDto("/api/v1/auth/register", registerRqDto);
        var token = performPostWithDto("/api/v1/auth/login", loginRqDto, LoginRsDto.class).getToken();

        // when
        var rqDto = TestFixtures.buildRegisterRqDto();
        var creditServiceRsDto = TestFixtures.buildUserInfoRqDto("admin@mail.ru");

        mockServer.expect(ExpectedCount.once(), requestTo(creditServiceUrl + "/credit-service/api/v1/create"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(rqDto)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(creditServiceRsDto), MediaType.APPLICATION_JSON));

        // then
        var rsDto = performPostWithDto("/credit-service/api/v1/create", rqDto, creditServiceRsDto.getClass(), status().isOk(), token);

        assertEquals(creditServiceRsDto.getEmail(), rsDto.getEmail());
    }
}