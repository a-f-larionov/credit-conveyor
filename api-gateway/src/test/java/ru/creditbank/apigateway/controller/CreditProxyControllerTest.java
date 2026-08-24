package ru.creditbank.apigateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    public void proxyCreateInvalidToken() {
        // given
        var token = "invalid-token";
        var rqDto = TestFixtures.buildRegisterRqDto();

        // when
        var rsDto = performPostWithDto("/credit-service/api/v1/create", rqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token Invalid", rsDto.error());
    }

    @Test
    public void proxyCreate() throws JsonProcessingException {
        // given
        var methodUrl = "/credit-service/api/v1/create";
        var registerRqDto = buildRegisterRqDto();
        var loginRqDto = buildLoginRqDto(registerRqDto);
        performPostWithDto("/api/v1/auth/register", registerRqDto);
        var token = performPostWithDto("/api/v1/auth/login", loginRqDto, LoginRsDto.class).token();

        // when
        var rqDto = buildRegisterRqDto();
        var creditServiceRsDto = TestFixtures.buildUserInfoRqDto("admin@mail.ru");

        mockServer.expect(ExpectedCount.once(), requestTo(creditServiceUrl + methodUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(objectMapper.writeValueAsString(rqDto)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(creditServiceRsDto), MediaType.APPLICATION_JSON));

        // then
        var rsDto = performPostWithDto(methodUrl, rqDto, creditServiceRsDto.getClass(), status().isOk(), token);

        assertEquals(creditServiceRsDto.email(), rsDto.email());
    }
}