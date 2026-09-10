package ru.creditbank.credit.operations;


import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpringBootMvcProxyBaseTest extends SpringBootMvcBaseTest {

    public static MockWebServer mockWebServer;

    @Autowired
    TestJwtGenerator jwtGenerator;

    @BeforeEach
    @SneakyThrows
    public void upServer() {
        mockWebServer = new MockWebServer();
        mockWebServer.start(8082);
    }

    @AfterEach
    @SneakyThrows
    public void shutdownServer() {
        mockWebServer.close();
        mockWebServer.shutdown();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    protected <RQ, RS> RS performGetMockedAndTestRequest(String url, RQ rqDto, RS rsDto, HttpStatus httpStatus) {
        enqueueResponse(rsDto, httpStatus);
        var token = jwtGenerator.generate();

        var actualRsDto = performGet(url, (Class<RS>) rsDto.getClass(), status().is(httpStatus.value()), token);

        testRequest(url, rqDto, token, HttpMethod.GET);

        return actualRsDto;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    protected <RQ, RS> RS performPostMockedAndTestRequest(String url, RQ rqDto, RS rsDto, HttpStatus httpStatus) {
        enqueueResponse(rsDto, httpStatus);
        var token = jwtGenerator.generate();

        var actualRsDto = performPost(url, rqDto, (Class<RS>) rsDto.getClass(), status().is(httpStatus.value()), token);

        testRequest(url, rqDto, token, HttpMethod.POST);

        return actualRsDto;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    protected <RQ, RS> RS performPatchMockedAndTestRequest(String url, RQ rqDto, RS rsDto, HttpStatus httpStatus) {
        enqueueResponse(rsDto, httpStatus);
        var token = jwtGenerator.generate();

        var actualRsDto = performPatch(url, rqDto, (Class<RS>) rsDto.getClass(), status().is(httpStatus.value()), token);

        testRequest(url, rqDto, token, HttpMethod.PATCH);

        return actualRsDto;
    }

    @SneakyThrows
    public <T> void enqueueResponse(T rsDto, HttpStatus httpStatus) {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(httpStatus.value())
                .setHeader("Content-Type", "application/json")
                .setBody(objectMapper.writeValueAsString(rsDto))
        );
    }

    @SneakyThrows
    public <RQ> void testRequest(String url, RQ rqDto, String token, HttpMethod httpMethod) {
        var request = mockWebServer.takeRequest();

        assertEquals(httpMethod.toString(), request.getMethod());
        assertEquals(url, request.getPath());
        assertEquals("Bearer " + token, request.getHeader("Authorization"));
        if (!httpMethod.equals(HttpMethod.GET)) {
            assertEquals(objectMapper.writeValueAsString(rqDto), request.getBody().readUtf8());
            assertEquals("application/json", request.getHeader("Content-Type"));
        }
    }
}
