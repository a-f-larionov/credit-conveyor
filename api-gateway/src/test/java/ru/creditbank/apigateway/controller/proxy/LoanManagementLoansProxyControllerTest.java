package ru.creditbank.apigateway.controller.proxy;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.creditbank.apigateway.SpringBootMvcProxyBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.common.library.dto.common.rs.ErrorRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.LoanRsDto;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.apigateway.TestFixtures.buildCreateLoanRqDto;
import static ru.creditbank.apigateway.TestFixtures.buildLoanRsDto;

class LoanManagementLoansProxyControllerTest extends SpringBootMvcProxyBaseTest {

    @Test
    public void proxyCreate_invalidToken_shouldReturnUnauthorized() {
        // given
        var url = "/loan-management-service/api/v1/loans/create";
        var rqDto = buildCreateLoanRqDto();
        var token = "invalid-token";

        // when
        var rsDto = performPost(url, rqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    public void proxyCreate() {
        // given
        var methodUrl = "/loan-management-service/api/v1/loans/create";
        var rqDto = buildCreateLoanRqDto();
        var httpStatus = HttpStatus.CREATED;
        var rsDto = TestFixtures.buildLoanRsDto();

        // when
        var actualRsDto = performPostMockedAndTestRequest(methodUrl, rqDto, rsDto, httpStatus);

        // then
        assertRsDtoEqualsRsDto(rsDto, actualRsDto);
    }

    @Test
    public void proxyInfo_invalidToken_shouldReturnUnauthorized() {
        // given
        var loanId = UUID.randomUUID();
        var url = "/loan-management-service/api/v1/loans/info/" + loanId;
        var token = "invalid-token";

        // when
        var rsDto = performGet(url, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    public void proxyInfo() {
        // given
        var loanId = UUID.randomUUID();
        var url = "/loan-management-service/api/v1/loans/info/" + loanId;
        var httpStatus = HttpStatus.OK;
        var rsDto = buildLoanRsDto();

        // when
        var actualRsDto = performGetMockedAndTestRequest(url, null, rsDto, httpStatus);

        // then
        assertRsDtoEqualsRsDto(rsDto, actualRsDto);
    }

    @Test
    public void proxyList_invalidToken_shouldReturnUnauthorized() {
        // given
        var userId = UUID.randomUUID();
        var url = "/loan-management-service/api/v1/loans/list/" + userId;
        var token = "invalid-token";

        // when
        var rsDto = performGet(url, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    public void proxyList() {
        // given
        var userId = UUID.randomUUID();
        var url = "/loan-management-service/api/v1/loans/list/" + userId;
        var httpStatus = HttpStatus.OK;
        var rsDto = TestFixtures.buildLoanListRsDto();

        // when
        var actualRsDto = performGetMockedAndTestRequest(url, null, rsDto, httpStatus);

        // then
        assertEquals(rsDto.loans().size(), actualRsDto.loans().size());

        for (int i = 0; i < rsDto.loans().size(); i++) {
            assertRsDtoEqualsRsDto(actualRsDto.loans().get(i), rsDto.loans().get(i));
        }
    }

    private void assertRsDtoEqualsRsDto(LoanRsDto rsDto, LoanRsDto actualRsDto) {
        assertEquals(rsDto.loanId(), actualRsDto.loanId());
        assertEquals(rsDto.userId(), actualRsDto.userId());
        assertEquals(rsDto.interestRate(), actualRsDto.interestRate());
        assertEquals(rsDto.nextPaymentDate().truncatedTo(ChronoUnit.SECONDS), actualRsDto.nextPaymentDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(rsDto.remainingAmount(), actualRsDto.remainingAmount());
        assertEquals(rsDto.totalAmount(), actualRsDto.totalAmount());
        assertEquals(rsDto.status(), actualRsDto.status());
    }
}