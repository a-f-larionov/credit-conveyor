package ru.creditbank.credit.operations.decision.rule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.ClientLoanPaymentsStatisticRsDto;

import java.util.UUID;

import static ru.creditbank.credit.operations.service.DecisionService.APPROVE_SCORES;

@Component
@Slf4j
public class EmploymentStabilityScoreRule implements CreditScoreRule {

    private static final int MIN_EMPLOYMENT_MONTHS = 6;

    @Override
    public Long evaluate(UUID creditId, CreditCreateRqDto request, ClientLoanPaymentsStatisticRsDto statistic) {
        return request.employmentMonths() <= MIN_EMPLOYMENT_MONTHS ?
                0 :
                APPROVE_SCORES + calcBonusScore(request.employmentMonths());
    }

    private int calcBonusScore(Integer employmentMonths) {
        // за каждый месяц один балл
        return Math.max(0, employmentMonths - MIN_EMPLOYMENT_MONTHS);
    }

    @Override
    public String getDescription() {
        return "Стаж работы не менее 6 месяцев";
    }
}