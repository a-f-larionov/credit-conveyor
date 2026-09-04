package ru.creditbank.apigateway.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.creditbank.apigateway.SpringBootMvcProxyBaseTest;
import ru.creditbank.common.library.dto.common.rs.ErrorRsDto;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.apigateway.TestFixtures.*;

class CreditProxyControllerTest extends SpringBootMvcProxyBaseTest {

    @Test
    public void proxyCreate_invalidToken_shouldReturnUnauthorized() {
        // given
        var url = "/credit-service/api/v1/create";
        var rqDto = buildRegisterRqDto();
        var token = "invalid-token";

        // when
        var rsDto = performPost(url, rqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    public void proxyCreate() {
        // given
        var methodUrl = "/credit-service/api/v1/create";
        var rqDto = buildCreateRqDto();
        var httpStatus = HttpStatus.CREATED;
        var rsDto = buildCreateRsDTo();

        // when
        var actualRsDto = performPostMockedAndTestRequest(methodUrl, rqDto, rsDto, httpStatus);

        // then
        assertEquals(rsDto.id(), actualRsDto.id());
        assertEquals(rsDto.createdAt().truncatedTo(ChronoUnit.SECONDS), actualRsDto.createdAt());
        assertEquals(rsDto.status(), actualRsDto.status());
    }

    @Test
    public void proxyStatusUpdate_invalidToken_shouldReturnUnauthorized() {
        // given
        var url = "/credit-service/api/v1/status/update/" + UUID.randomUUID();
        var rqDto = buildRegisterRqDto();
        var token = "invalid-token";

        // when
        var rsDto = performPatch(url, rqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    public void proxyStatusUpdate() {
        // given
        var url = "/credit-service/api/v1/status/update/" + UUID.randomUUID();
        var rqDto = buildUpdateStatusRqDto();
        var rsDto = buildCreateRsDTo();
        var httpStatus = HttpStatus.OK;

        // when
        var actualRsDto = performPatchMockedAndTestRequest(url, rqDto, rsDto, httpStatus);

        // then
        assertEquals(rsDto.id(), actualRsDto.id());
        assertEquals(rsDto.createdAt().truncatedTo(ChronoUnit.SECONDS), actualRsDto.createdAt());
        assertEquals(rsDto.status(), actualRsDto.status());
    }
}