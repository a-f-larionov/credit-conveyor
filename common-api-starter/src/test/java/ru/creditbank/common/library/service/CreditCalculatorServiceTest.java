package ru.creditbank.common.library.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.creditbank.common.library.autoconfigure.CreditAutoConfiguration;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = CreditAutoConfiguration.class)
@ActiveProfiles({"test", "test-local"})
class CreditCalculatorServiceTest {

    @Autowired
    CreditCalculatorService creditCalculatorService;

    @Test
    void calcPrincipal() {
        // given
        var monthlyPayment = new BigDecimal("1000");
        var interestFactor = new BigDecimal("10");

        // when
        var result = creditCalculatorService.calcPrincipal(monthlyPayment, interestFactor);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("990"));
    }

    @Test
    void calcInterest() {
        // given
        var sum = new BigDecimal("1000");
        var monthlyFactor = new BigDecimal("0.123456789");

        // when
        var result = creditCalculatorService.calcInterest(sum, monthlyFactor);

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
        var result = creditCalculatorService.getMonthlyPayment(termMonths, totalAmount, monthlyFactor);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("16517.12"));
    }

    @Test
    void getAnnuityFactor() {
        // given
        Integer termMonths = 22;
        BigDecimal monthlyFactor = new BigDecimal("0.123456789");

        // when
        var result = creditCalculatorService.getAnnuityFactor(termMonths, monthlyFactor);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("0.1337886632"));
    }

    @Test
    void getMonthlyFactor() {
        // given
        var interestRate = new BigDecimal("12.3456789");

        // when
        var result = creditCalculatorService.getMonthlyFactor(interestRate);

        // then
        assertThat(result).isEqualByComparingTo(new BigDecimal("0.0102880658"));
    }

    @Test
    void getDateForMonth() {
        // given
        var firstPaymentDate = now();
        var month = 25;

        // when
        var result = creditCalculatorService.getDateForMonth(firstPaymentDate, month);

        // then
        assertThat(result).isEqualTo(
                now().plus(month * 30, ChronoUnit.DAYS)
                        .truncatedTo(ChronoUnit.DAYS)
        );
    }
}