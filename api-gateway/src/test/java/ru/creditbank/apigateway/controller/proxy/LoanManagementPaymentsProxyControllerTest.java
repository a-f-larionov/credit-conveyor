package ru.creditbank.apigateway.controller.proxy;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.creditbank.apigateway.SpringBootMvcProxyBaseTest;
import ru.creditbank.apigateway.TestFixtures;
import ru.creditbank.common.library.dto.common.rs.ErrorRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.PaymentHistoryItemRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.PaymentHistoryRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.PaymentRsDto;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.apigateway.TestFixtures.buildPaymentRqDto;

class LoanManagementPaymentsProxyControllerTest extends SpringBootMvcProxyBaseTest {

    @Test
    public void proxyProcess_invalidToken_shouldReturnUnauthorized() {
        // given
        var loanId = UUID.randomUUID();
        var url = "/loan-management-service/api/v1/payments/process";
        var rqDto = buildPaymentRqDto(loanId);
        var token = "invalid-token";

        // when
        var rsDto = performPost(url, rqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    public void proxyProcess() {
        // given
        var loanId = UUID.randomUUID();
        var methodUrl = "/loan-management-service/api/v1/payments/process";
        var rqDto = buildPaymentRqDto(loanId);
        var httpStatus = HttpStatus.CREATED;
        var rsDto = TestFixtures.buildPaymentRsDto();

        // when
        var actualRsDto = performPostMockedAndTestRequest(methodUrl, rqDto, rsDto, httpStatus);

        // then
        assertPaymentRsDtoEquals(rsDto, actualRsDto);
    }

    @Test
    public void proxyHistory_invalidToken_shouldReturnUnauthorized() {
        // given
        var loanId = UUID.randomUUID();
        var url = "/loan-management-service/api/v1/payments/history/" + loanId;
        var token = "invalid-token";

        // when
        var rsDto = performGet(url, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    public void proxyHistory() {
        // given
        var loanId = UUID.randomUUID();
        var url = "/loan-management-service/api/v1/payments/history/" + loanId;
        var httpStatus = HttpStatus.OK;
        var rsDto = TestFixtures.buildPaymentHistoryRsDto();

        // when
        var actualRsDto = performGetMockedAndTestRequest(url, null, rsDto, httpStatus);

        // then
        assertPaymentHistoryRsDtoEquals(rsDto, actualRsDto);
    }

    private void assertPaymentRsDtoEquals(PaymentRsDto expected, PaymentRsDto actual) {
        assertEquals(expected.paymentId(), actual.paymentId());
        assertEquals(expected.nextPaymentDate().truncatedTo(ChronoUnit.SECONDS), actual.nextPaymentDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(expected.principalRemainingAmount(), actual.principalRemainingAmount());
    }

    private void assertPaymentItemRsDtoEquals(PaymentHistoryItemRsDto expected, PaymentHistoryItemRsDto actual) {
        assertEquals(expected.paymentId(), actual.paymentId());
        assertEquals(expected.amount(), actual.amount());
        assertEquals(expected.datetime().truncatedTo(ChronoUnit.SECONDS), actual.datetime().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(expected.newBalance(), actual.newBalance());
        assertEquals(expected.type(), actual.type());
    }

    private void assertPaymentHistoryRsDtoEquals(PaymentHistoryRsDto expected, PaymentHistoryRsDto actual) {
        assertEquals(expected.payments().size(), actual.payments().size());
        for (int i = 0; i < expected.payments().size(); i++) {
            assertPaymentItemRsDtoEquals(expected.payments().get(i), actual.payments().get(i));
        }
    }
}