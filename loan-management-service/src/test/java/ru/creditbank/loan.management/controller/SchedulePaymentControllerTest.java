package ru.creditbank.loan.management.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.loan.management.SpringBootMvcBaseTest;
import ru.creditbank.loan.management.TestJwtGenerator;
import ru.creditbank.loan.management.dto.rq.CreateLoanRqDto;
import ru.creditbank.loan.management.dto.rs.LoanPaymentScheduleRsDto;
import ru.creditbank.loan.management.dto.rs.LoanPaymentsScheduleListRsDto;
import ru.creditbank.loan.management.dto.rs.LoanRsDto;
import ru.creditbank.loan.management.enums.PaymentStatusEnum;
import ru.creditbank.loan.management.service.PaymentScheduleGeneratorService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.common.library.enums.UserRole.ROLE_CREDIT_MANAGER;

public class SchedulePaymentControllerTest extends SpringBootMvcBaseTest {

    @Autowired
    TestJwtGenerator jwtGenerator;

    @Autowired
    PaymentScheduleGeneratorService paymentScheduleGeneratorService;

    @Test
    public void getSchedule() {
        // given
        var userId = UUID.randomUUID();
        var totalAmount = new BigDecimal("1000000");
        var termMonths = 12;
        var interestRate = new BigDecimal("12.0");
        var managerToken = jwtGenerator.generate(Set.of(ROLE_CREDIT_MANAGER));
        var rqDto = CreateLoanRqDto.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .interestRate(interestRate)
                .termMonths(termMonths)
                .firstPaymentDate(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        // when
        var createdRsDto = performPost("/loan-management-service/api/v1/loans/create", rqDto, LoanRsDto.class, status().isCreated(), managerToken);
        var scheduleRsDto = performGet("/loan-management-service/api/v1/schedule-payment/list/" + createdRsDto.loanId(), LoanPaymentsScheduleListRsDto.class, status().isOk(), managerToken);

        // then
        assertThat(scheduleRsDto.loanId()).isEqualTo(createdRsDto.loanId());
        assertThat(scheduleRsDto.payments()).hasSize(termMonths);
        scheduleRsDto.payments().sort(Comparator.comparing(LoanPaymentScheduleRsDto::date));

        var monthlyRate = paymentScheduleGeneratorService.getMonthlyFactor(interestRate);
        var monthlyPayment = paymentScheduleGeneratorService.getMonthlyPayment(termMonths, totalAmount, monthlyRate);
        var remainAmount = totalAmount;

        var totalPaymentSum = BigDecimal.ZERO;

        for (int months = 1; months <= termMonths; months++) {
            var rsDto = scheduleRsDto.payments().get(months - 1);

            var interest = paymentScheduleGeneratorService.calcInterest(remainAmount, monthlyRate);
            var principal = paymentScheduleGeneratorService.calcPrincipal(monthlyPayment, interest);
            if (months == termMonths) {
                principal = remainAmount;
                interest = paymentScheduleGeneratorService.calcInterest(remainAmount, monthlyRate);
                remainAmount = BigDecimal.ZERO;
            } else {
                remainAmount = remainAmount.subtract(principal);
            }

            assertThat(rsDto.id()).isNotNull();
            assertThat(rsDto.number()).isEqualTo(months);
            assertThat(rsDto.loanId()).isEqualTo(createdRsDto.loanId());
            assertThat(rsDto.date()).isEqualTo(rqDto.firstPaymentDate().plus((months * 30L), ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS));
            assertThat(rsDto.interestAmount()).isEqualByComparingTo(interest);
            assertThat(rsDto.principalAmount()).isEqualByComparingTo(principal);
            assertThat(rsDto.remainAmount()).isEqualByComparingTo(remainAmount);
            assertThat(rsDto.status()).isEqualTo(PaymentStatusEnum.PENDING);

            totalPaymentSum = totalPaymentSum.add(interest).add(principal);
        }

        assertThat(totalPaymentSum).isEqualByComparingTo(new BigDecimal("1066185.45"));
    }
}
