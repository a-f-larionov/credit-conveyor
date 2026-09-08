package ru.creditbank.loan.management.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.common.library.dto.loan.management.rq.CreateLoanRqDto;
import ru.creditbank.common.library.dto.loan.management.rq.PaymentRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.*;
import ru.creditbank.common.library.enums.LoanStatusEnum;
import ru.creditbank.common.library.enums.UserRole;
import ru.creditbank.loan.management.SpringBootMvcBaseTest;
import ru.creditbank.loan.management.TestJwtGenerator;
import ru.creditbank.loan.management.dto.rq.CorrectDateRqDto;
import ru.creditbank.loan.management.dto.rs.LoanPaymentScheduleRsDto;
import ru.creditbank.loan.management.dto.rs.LoanPaymentsScheduleListRsDto;
import ru.creditbank.loan.management.schedule.OverdueScheduleService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.common.library.enums.PaymentTypeEnum.FULL;
import static ru.creditbank.common.library.enums.PaymentTypeEnum.REGULAR;
import static ru.creditbank.loan.management.TestFixtures.buildCreateLoanRqDto;
import static ru.creditbank.loan.management.TestFixtures.buildPaymentsRqDto;

public class PaymentControllerTest extends SpringBootMvcBaseTest {

    @Autowired
    TestJwtGenerator jwtGenerator;

    @Autowired
    OverdueScheduleService overdueScheduleService;

    @Test
    void processPaymentRegularAndCloseLoan() {
        // given
        var userId = UUID.randomUUID();
        var userToken = jwtGenerator.generate(userId);
        var tokenManager = jwtGenerator.generate(Set.of(UserRole.ROLE_CREDIT_MANAGER));
        var loanRqDto = buildCreateLoanRqDto(userId, new BigDecimal("1000000"), 3, new BigDecimal("12"));
        var rsDto = performPost("/loan-management-service/api/v1/loans/create", loanRqDto, LoanRsDto.class, status().isCreated(), tokenManager);
        var loanId = rsDto.loanId();

        // when - then  1/3 regular payment
        var rqDto = buildPaymentsRqDto(loanId, new BigDecimal("340022.11"), REGULAR);
        var paymentRsDto1 = performPost("/loan-management-service/api/v1/payments/process", rqDto, PaymentRsDto.class, userToken);

        assertPaymentRsDtoIs(paymentRsDto1, loanRqDto, 60, new BigDecimal("669977.89"));

        // asser is not closed
        assertThat(performGet("/loan-management-service/api/v1/loans/info/" + rsDto.loanId(), LoanRsDto.class, userToken)
                .status())
                .isEqualTo(LoanStatusEnum.ACTIVE);

        // when - then  2/3 regular payment
        rqDto = buildPaymentsRqDto(loanId, new BigDecimal("340022.11"), REGULAR);
        var paymentRsDto2 = performPost("/loan-management-service/api/v1/payments/process", rqDto, PaymentRsDto.class, userToken);
        assertPaymentRsDtoIs(paymentRsDto2, loanRqDto, 90, new BigDecimal("336655.56"));

        // asser is not closed
        assertThat(performGet("/loan-management-service/api/v1/loans/info/" + rsDto.loanId(), LoanRsDto.class, userToken)
                .status())
                .isEqualTo(LoanStatusEnum.ACTIVE);

        // when - then  3/3 regular payment
        rqDto = buildPaymentsRqDto(loanId, new BigDecimal("340022.12"), REGULAR);
        var paymentRsDto3 = performPost("/loan-management-service/api/v1/payments/process", rqDto, PaymentRsDto.class, userToken);
        assertPaymentRsDtoIs(paymentRsDto3, loanRqDto, null, BigDecimal.ZERO);

        // then loan is closed
        var loanRsDto = performGet("/loan-management-service/api/v1/loans/info/" + rsDto.loanId(), LoanRsDto.class, userToken);
        assertThat(loanRsDto.status())
                .isEqualTo(LoanStatusEnum.CLOSED);
    }

    @Test
    void processPaymentFullAndCloseLoan() {
        // given
        var userId = UUID.randomUUID();
        var userToken = jwtGenerator.generate(userId);
        var tokenManager = jwtGenerator.generate(Set.of(UserRole.ROLE_CREDIT_MANAGER));
        var loanRqDto = buildCreateLoanRqDto(userId, new BigDecimal("1000000"), 3, new BigDecimal("12"));
        var rsDto = performPost("/loan-management-service/api/v1/loans/create", loanRqDto, LoanRsDto.class,
                status().isCreated(), tokenManager);
        var loanId = rsDto.loanId();

        // when - then  1/3 regular payment
        var rqDto = buildPaymentsRqDto(loanId, new BigDecimal("1020066.34"), FULL);
        var paymentRsDto1 = performPost("/loan-management-service/api/v1/payments/process", rqDto, PaymentRsDto.class, userToken);

        assertPaymentRsDtoIs(paymentRsDto1, loanRqDto, null, BigDecimal.ZERO);

        // then loan is closed
        var loanRsDto = performGet("/loan-management-service/api/v1/loans/info/" + rsDto.loanId(), LoanRsDto.class, userToken);
        assertThat(loanRsDto.status())
                .isEqualTo(LoanStatusEnum.CLOSED);
    }

    @Test
    void history() {
        // given
        var userId = UUID.randomUUID();
        var userToken = jwtGenerator.generate(userId);

        var tokenManager = jwtGenerator.generate(Set.of(UserRole.ROLE_CREDIT_MANAGER));
        var loanRqDto = buildCreateLoanRqDto(userId, new BigDecimal("1000000"), 2, new BigDecimal("12"));
        var rsDto = performPost("/loan-management-service/api/v1/loans/create", loanRqDto, LoanRsDto.class, status().isCreated(), tokenManager);
        var loanId = rsDto.loanId();

        var paymentRqDto1 = buildPaymentsRqDto(loanId, new BigDecimal("507512.44"), REGULAR);
        var paymentRqDto2 = buildPaymentsRqDto(loanId, new BigDecimal("507512.44"), REGULAR);
        var paymentRs1 = performPost("/loan-management-service/api/v1/payments/process", paymentRqDto1, PaymentRsDto.class, userToken);
        var paymentRs2 = performPost("/loan-management-service/api/v1/payments/process", paymentRqDto2, PaymentRsDto.class, userToken);

        // when
        var paymentsHistory = performGet("/loan-management-service/api/v1/payments/history/" + loanId, PaymentHistoryRsDto.class, userToken);

        // then
        assertThat(paymentsHistory.payments()).hasSize(2);

        var rsHistoryDto1 = findByRqDtoFielAmount(paymentsHistory.payments(), paymentRs1.paymentId());
        var rsHistoryDto2 = findByRqDtoFielAmount(paymentsHistory.payments(), paymentRs2.paymentId());

        assertHistoryDtoIs(rsHistoryDto1, paymentRqDto1, new BigDecimal("502487.56"));
        assertHistoryDtoIs(rsHistoryDto2, paymentRqDto2, BigDecimal.ZERO);
    }

    @Test
    void statistic() {
        // given
        var userId = UUID.randomUUID();
        var userToken = jwtGenerator.generate(userId);

        // create loan
        var tokenManager = jwtGenerator.generate(Set.of(UserRole.ROLE_CREDIT_MANAGER));
        var loanRqDto = buildCreateLoanRqDto(userId, new BigDecimal("1000000"), 2, new BigDecimal("12"));
        var rsDto = performPost("/loan-management-service/api/v1/loans/create", loanRqDto, LoanRsDto.class, status().isCreated(), tokenManager);
        var loanId = rsDto.loanId();

        // correct any payment
        var scheduleRsDto = performGet("/loan-management-service/api/v1/schedule-payment/list/" + loanId, LoanPaymentsScheduleListRsDto.class, status().isOk(), tokenManager);
        var anyPayment = scheduleRsDto.payments().stream().findAny().orElseThrow(AssertionError::new);
        performPatch("/loan-management-service/api/v1/schedule-payment/correct-date/" + anyPayment.id(),
                CorrectDateRqDto.builder().newDate(Instant.now().minus(100, DAYS)).build(),
                LoanPaymentScheduleRsDto.class,
                tokenManager);

        // when
        overdueScheduleService.checkOverdueSchedulePayments();

        // paid all payments
        performPost("/loan-management-service/api/v1/payments/process", buildPaymentsRqDto(loanId, new BigDecimal("507512.44"), REGULAR), PaymentRsDto.class, userToken);
        performPost("/loan-management-service/api/v1/payments/process", buildPaymentsRqDto(loanId, new BigDecimal("507512.44"), REGULAR), PaymentRsDto.class, userToken);


        var statisticRsDto = performGet("/loan-management-service/api/v1/payments/user-statistic/" + userId, ClientLoanPaymentsStatisticRsDto.class, status().isOk(), userToken);

        // then
        assertThat(statisticRsDto.allOverduePayments()).isEqualTo(1);
        assertThat(statisticRsDto.allDonePayments()).isEqualTo(2);
    }

    private void assertHistoryDtoIs(PaymentHistoryItemRsDto rsDto1, PaymentRqDto rqDto, BigDecimal remaining) {
        assertThat(rsDto1.paymentId()).isNotNull();
        assertThat(rsDto1.amount()).isEqualByComparingTo(rqDto.amount());
        assertThat(rsDto1.datetime()).isBetween(now().minus(10, ChronoUnit.MINUTES), now());
        assertThat(rsDto1.type()).isEqualTo(rqDto.type());
        assertThat(rsDto1.newBalance()).isEqualByComparingTo(remaining);
    }

    private PaymentHistoryItemRsDto findByRqDtoFielAmount(List<PaymentHistoryItemRsDto> list, UUID needlePaymentId) {
        return list.stream()
                .filter(item ->
                        item.paymentId().compareTo(needlePaymentId) == 0
                )
                .findAny()
                .orElse(null);
    }

    private static void assertPaymentRsDtoIs(PaymentRsDto paymentRsDto1, CreateLoanRqDto loanRqDto, Integer amountToAdd, BigDecimal principalRemainingAmount) {
        assertThat(paymentRsDto1.paymentId()).isNotNull();
        if (amountToAdd == null) {
            assertThat(paymentRsDto1.nextPaymentDate()).isNull();
        } else {
            assertThat(paymentRsDto1.nextPaymentDate())
                    .isEqualTo(loanRqDto.firstPaymentDate().plus(amountToAdd, DAYS).truncatedTo(DAYS));
        }
        assertThat(paymentRsDto1.principalRemainingAmount()).isEqualByComparingTo(principalRemainingAmount);
    }
}
