package ru.creditbank.loan.management.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.creditbank.common.library.enums.SchedulePaymentStatusEnum;
import ru.creditbank.common.library.service.CreditCalculatorService;
import ru.creditbank.loan.management.enitity.LoanEntity;
import ru.creditbank.loan.management.enitity.SchedulePaymentEntity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentScheduleGeneratorService {

    public final CreditCalculatorService creditCalculatorService;

    public List<SchedulePaymentEntity> generateSchedulePayments(
            @NonNull Instant firstPayDate,
            @NonNull Integer termMonths,
            @NonNull BigDecimal totalAmount,
            @NonNull BigDecimal interestRate,
            LoanEntity loanEntity) {

        validateParams(firstPayDate, termMonths, totalAmount, interestRate);

        var monthlyFactor = creditCalculatorService.getMonthlyFactor(interestRate);

        List<SchedulePaymentEntity> schedule = new ArrayList<>();

        var remainingPrincipal = totalAmount;
        var monthlyPayment = creditCalculatorService.getMonthlyPayment(termMonths, totalAmount, monthlyFactor);

        for (int month = 1; month <= termMonths; month++) {
            var interest = creditCalculatorService.calcInterest(remainingPrincipal, monthlyFactor);
            var principal = creditCalculatorService.calcPrincipal(monthlyPayment, interest);

            if (month == termMonths) { // last month correction
                principal = remainingPrincipal;
                interest = creditCalculatorService.calcInterest(remainingPrincipal, monthlyFactor);
                remainingPrincipal = BigDecimal.ZERO;
            } else {
                remainingPrincipal = remainingPrincipal.subtract(principal).setScale(2, RoundingMode.HALF_UP);
            }

            schedule.add(buildEntity(firstPayDate, month, interest, principal, remainingPrincipal, loanEntity));
        }

        return schedule;
    }

    private SchedulePaymentEntity buildEntity(
            Instant firstPayDate, int month, BigDecimal interest, BigDecimal principal,
            BigDecimal remain, LoanEntity loanEntity) {
        return SchedulePaymentEntity.builder()
                .loan(loanEntity)
                .userId(loanEntity.getUserId())
                .number(month)
                .date(creditCalculatorService.getDateForMonth(firstPayDate, month))
                .principalAmount(principal)
                .interestAmount(interest)
                .remainAmount(remain)
                .status(SchedulePaymentStatusEnum.PENDING)
                .createdAt(Instant.now())
                .overdueDays(0L)
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
