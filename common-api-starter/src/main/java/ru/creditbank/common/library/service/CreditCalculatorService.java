package ru.creditbank.common.library.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class CreditCalculatorService {

    @NonNull
    public BigDecimal calcPrincipal(BigDecimal monthlyPayment, BigDecimal interest) {
        var principalPart = monthlyPayment.subtract(interest);
        if (principalPart.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }
        return principalPart.setScale(2, RoundingMode.HALF_UP);
    }

    @NonNull
    public BigDecimal calcInterest(BigDecimal sum, BigDecimal monthlyFactor) {
        return sum.multiply(monthlyFactor).setScale(2, RoundingMode.HALF_UP);
    }

    @NonNull
    public BigDecimal getMonthlyPayment(Integer termMonths, BigDecimal totalAmount, BigDecimal monthlyFactor) {
        var annuityFactor = getAnnuityFactor(termMonths, monthlyFactor);
        return totalAmount.multiply(annuityFactor)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @NonNull
    public BigDecimal getMonthlyFactor(BigDecimal interestRate) {
        return interestRate
                .divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);
    }

    @NonNull
    public BigDecimal getAnnuityFactor(Integer months, BigDecimal monthlyFactor) {
        var onePlusR = BigDecimal.ONE.add(monthlyFactor);
        var pow = onePlusR.pow(months, MathContext.DECIMAL64);
        return monthlyFactor.multiply(pow)
                .divide(pow.subtract(BigDecimal.ONE), 10, RoundingMode.HALF_UP);
    }

    @NonNull
    public Instant getDateForMonth(Instant firstPaymentDate, int month) {
        return firstPaymentDate.plus((month * 30L), ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
    }
}
