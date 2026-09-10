package ru.creditbank.credit.operations.decision.rule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditbank.credit.operations.dto.ScoringInputDto;

import static ru.creditbank.credit.operations.service.AutoDecisionService.APPROVE_SCORES;

@Component
@Slf4j
public class EmploymentStabilityScoreRule implements CreditScoreRule {

    private static final int MIN_EMPLOYMENT_MONTHS = 6;

    @Override
    public String getDescription() {
        return "Стаж работы не менее 6 месяцев";
    }

    @Override
    public Long evaluate(ScoringInputDto scoringInputDto) {
        return scoringInputDto.employmentMonths() <= MIN_EMPLOYMENT_MONTHS ?
                0 :
                APPROVE_SCORES + calcBonusScore(scoringInputDto.employmentMonths());
    }

    private int calcBonusScore(Integer employmentMonths) {
        // за каждый месяц один балл
        return Math.max(0, employmentMonths - MIN_EMPLOYMENT_MONTHS);
    }
}