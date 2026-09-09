package ru.creditbank.credit.operations.decision.rule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.creditbank.common.library.service.CreditCalculatorService;
import ru.creditbank.credit.operations.dto.ScoringInputDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.math.BigDecimal.ZERO;
import static ru.creditbank.credit.operations.service.AutoDecisionService.APPROVE_SCORES;

@Component
@RequiredArgsConstructor
@Slf4j
public class IncomeCreditScoreRule implements CreditScoreRule {

    private static final BigDecimal MAX_PAYMENT_RATIO = new BigDecimal("0.4");
    private static final BigDecimal BONUS_SCORE_PER_EVERY_SALARY = new BigDecimal("10000");

    @Value("${credit.base-interest-rate:10.0}")
    private BigDecimal baseInterestRate;

    private final CreditCalculatorService creditCalculatorService;

    @Override
    public Long evaluate(ScoringInputDto scoringInputDto) {
        var monthlyFactor = creditCalculatorService.getMonthlyFactor(baseInterestRate);

        var creditMonthlyPayment = creditCalculatorService.getMonthlyPayment(
                scoringInputDto.termMonths(),
                scoringInputDto.requestedAmount(),
                monthlyFactor
        );

        var monthlyIncome = scoringInputDto.monthlyIncome();
        validateArguments(monthlyIncome, creditMonthlyPayment);

        var ratio = monthlyIncome.divide(creditMonthlyPayment, 4, RoundingMode.HALF_UP);

        return ratio.compareTo(MAX_PAYMENT_RATIO) <= 0 ?
                0L :
                APPROVE_SCORES + calcBonusScore(monthlyIncome, creditMonthlyPayment);
    }

    private static void validateArguments(BigDecimal monthlyIncome, BigDecimal creditMonthlyPayment) {
        if (monthlyIncome == null || monthlyIncome.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("The monthly income must be positive");
        }

        if (creditMonthlyPayment.compareTo(ZERO) <= 0) {
            throw new IllegalArgumentException("The monthly payment must be positive.");
        }
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