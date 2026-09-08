package ru.creditbank.credit.operations.decision.rule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.ClientLoanPaymentsStatisticRsDto;
import ru.creditbank.common.library.service.CreditCalculatorService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static ru.creditbank.credit.operations.service.DecisionService.APPROVE_SCORES;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncomeCreditScoreRule implements CreditScoreRule {


    @Value("${credit.auto-decision.default-interest}")
    private BigDecimal interestRate;
    private static final BigDecimal BONUS_SCORE_PER_EVERY_SALARY = new BigDecimal("10000");
    private static final BigDecimal MAX_PAYMENT_RATIO = new BigDecimal("0.4");

    private final CreditCalculatorService creditCalculatorService;

    @Override
    public Long evaluate(UUID creditId, CreditCreateRqDto request, ClientLoanPaymentsStatisticRsDto statistic) {
        var monthlyFactor = creditCalculatorService.getMonthlyFactor(interestRate);
        int months = request.employmentMonths();
        var amount = request.requestedAmount();

        var creditMonthlyPayment = creditCalculatorService.getMonthlyPayment(
                months,
                amount,
                monthlyFactor
        );
        if (BigDecimal.ZERO.compareTo(creditMonthlyPayment) == 0) {
            throw new IllegalStateException();
        }

        var ratio = request.monthlyIncome().divide(creditMonthlyPayment, 4, RoundingMode.HALF_UP);

        return ratio.compareTo(MAX_PAYMENT_RATIO) <= 0 ?
                0L :
                APPROVE_SCORES + calcBonusScore(request.monthlyIncome(), creditMonthlyPayment);
    }

    private long calcBonusScore(BigDecimal monthlyIncome, BigDecimal creditMonthlyPayment) {
        return Math.max(
                0,
                monthlyIncome.subtract(creditMonthlyPayment)
                        .divide(BONUS_SCORE_PER_EVERY_SALARY, 0, RoundingMode.HALF_UP)
                        .longValue()
        );
    }

    @Override
    public String getDescription() {
        return "Ежемесячный платёж не превышает " + MAX_PAYMENT_RATIO.multiply(new BigDecimal(100)) + "% от дохода";
    }
}