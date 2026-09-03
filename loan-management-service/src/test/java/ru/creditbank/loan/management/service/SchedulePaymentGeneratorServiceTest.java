package ru.creditbank.loan.management.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test", "test-local"})
class SchedulePaymentGeneratorServiceTest {

    @Autowired
    PaymentScheduleGeneratorService paymentScheduleGeneratorService;

    @Test
    void calcPrincipal() {
        // given
        var monthlyPayment = new BigDecimal("1000");
        var interestFactor = new BigDecimal("10");

        // when
        var result = paymentScheduleGeneratorService.calcPrincipal(monthlyPayment, interestFactor);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("990"));
    }

    @Test
    void calcInterest() {
        // given
        var sum = new BigDecimal("1000");
        var monthlyFactor = new BigDecimal("0.123456789");

        // when
        var result = paymentScheduleGeneratorService.calcInterest(sum, monthlyFactor);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("123.46"));
    }

    @Test
    void getMonthlyPayment() {
        // given
        Integer termMonths = 22;
        BigDecimal totalAmount = new BigDecimal("123456.78");
        BigDecimal monthlyFactor = new BigDecimal("0.123456789");

        // when
        var result = paymentScheduleGeneratorService.getMonthlyPayment(termMonths, totalAmount, monthlyFactor);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("16517.12"));
    }

    @Test
    void getAnnuityFactor() {
        // given
        Integer termMonths = 22;
        BigDecimal monthlyFactor = new BigDecimal("0.123456789");

        // when
        var result = paymentScheduleGeneratorService.getAnnuityFactor(termMonths, monthlyFactor);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("0.1337886632"));
    }

    @Test
    void getMonthlyFactor() {
        // given
        var interestRate = new BigDecimal("12.3456789");

        // when
        var result = paymentScheduleGeneratorService.getMonthlyFactor(interestRate);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("0.0102880658"));
    }

    @Test
    void getDateForMonth() {
        // given
        var firstPaymentDate = now();
        var month = 25;

        // when
        var result = paymentScheduleGeneratorService.getDateForMonth(firstPaymentDate, month);

        // then
        assertThat(result).isEqualTo(
                now().plus(month * 30, ChronoUnit.DAYS)
                        .truncatedTo(ChronoUnit.DAYS)
        );
    }
}