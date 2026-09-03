package ru.creditbank.loan.management.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.creditbank.loan.management.enitity.LoanEntity;
import ru.creditbank.loan.management.enitity.SchedulePaymentEntity;
import ru.creditbank.loan.management.enums.PaymentStatusEnum;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentScheduleGeneratorService {

    public List<SchedulePaymentEntity> generateSchedulePayments(
            @NonNull Instant firstPayDate,
            @NonNull Integer termMonths,
            @NonNull BigDecimal totalAmount,
            @NonNull BigDecimal interestRate,
            LoanEntity loanEntity) {

        validateParams(firstPayDate, termMonths, totalAmount, interestRate);

        var monthlyFactor = getMonthlyFactor(interestRate);

        List<SchedulePaymentEntity> schedule = new ArrayList<>();

        var remainingPrincipal = totalAmount;
        var monthlyPayment = getMonthlyPayment(termMonths, totalAmount, monthlyFactor);

        for (int month = 1; month <= termMonths; month++) {
            var interest = calcInterest(remainingPrincipal, monthlyFactor);
            var principal = calcPrincipal(monthlyPayment, interest);

            if (month == termMonths) { // last month correction
                principal = remainingPrincipal;
                interest = calcInterest(remainingPrincipal, monthlyFactor);
                remainingPrincipal = BigDecimal.ZERO;
            } else {
                remainingPrincipal = remainingPrincipal.subtract(principal).setScale(2, RoundingMode.HALF_UP);
            }

            schedule.add(buildEntity(firstPayDate, month, interest, principal, remainingPrincipal, loanEntity));
        }

        return schedule;
    }

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

    private SchedulePaymentEntity buildEntity(Instant firstPayDate, int month, BigDecimal interest, BigDecimal principal, BigDecimal remain, LoanEntity loanEntity) {
        return SchedulePaymentEntity.builder()
                .loan(loanEntity)
                .number(month)
                .date(getDateForMonth(firstPayDate, month))
                .principalAmount(principal)
                .interestAmount(interest)
                .remainAmount(remain)
                .status(PaymentStatusEnum.PENDING)
                .createdAt(Instant.now())
                .build();
    }

    private void validateParams(Instant firstPayDate, Integer termMonths, BigDecimal totalAmount, BigDecimal interestRate) {
        if (firstPayDate == null) {
            throw new IllegalArgumentException("firstPayDate must not be null");
        }
        if (termMonths == null || termMonths <= 0) {
            throw new IllegalArgumentException("termMonths must be positive");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("totalAmount must be positive");
        }
        if (interestRate == null || interestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("interestRate must be non-negative");
        }
    }
}
