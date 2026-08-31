package ru.creditbank.apigateway.controller;

import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import ru.creditbank.apigateway.SpringBootMvcBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.common.library.dto.rs.ErrorRsDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.apigateway.TestFixtures.buildLoginRqDto;
import static ru.creditbank.apigateway.TestFixtures.buildRegisterRqDto;

class CreditProxyControllerTest extends SpringBootMvcBaseTest {

    @Test
    public void proxyCreateInvalidToken() {
        // given
        var token = "invalid-token";
        var rqDto = TestFixtures.buildRegisterRqDto();

        // when
        var rsDto = performPostWithDto("/credit-service/api/v1/create", rqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    public void proxyCreate() throws IOException, InterruptedException {
        // given
        var methodUrl = "/credit-service/api/v1/create";
        var token = getValidToken();

        var rqDto = buildRegisterRqDto();
        var rsDto = TestFixtures.buildRsDto();

        var mockWebServer = createMockWebServerAndEnqueue(rsDto);

        // when
        var actualRsDto = performPostWithDto(methodUrl, rqDto, rsDto.getClass(), status().isOk(), token);

        // then
        var request = mockWebServer.takeRequest();

        assertEquals("POST", request.getMethod());
        assertEquals("/credit-service/api/v1/create", request.getPath());
        assertEquals(objectMapper.writeValueAsString(rqDto), request.getBody().readUtf8());
        assertEquals("Bearer " + token, request.getHeader("Authorization"));

        assertEquals(rsDto.email(), actualRsDto.email());

        mockWebServer.close();
    }

    @NotNull
    @SneakyThrows
    private <T> MockWebServer createMockWebServerAndEnqueue(T rsDto) {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start(8082);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(rsDto))
        );
        return mockWebServer;
    }

    private String getValidToken() {
        var registerRqDto = buildRegisterRqDto();
        var loginRqDto = buildLoginRqDto(registerRqDto);
        performPostWithDto("/api/v1/auth/register", registerRqDto);
        return performPostWithDto("/api/v1/auth/login", loginRqDto, LoginRsDto.class).token();
    }
}