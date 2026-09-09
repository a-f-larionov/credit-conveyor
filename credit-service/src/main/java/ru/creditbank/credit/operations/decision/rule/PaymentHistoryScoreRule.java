package ru.creditbank.credit.operations.decision.rule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditbank.credit.operations.dto.ScoringInputDto;

import static ru.creditbank.credit.operations.service.AutoDecisionService.APPROVE_SCORES;

@Component
@Slf4j
public class PaymentHistoryScoreRule implements CreditScoreRule {

    private static final double MAX_OVERDUED_PAYMENTS_RATIO = 2;

    @Override
    public Long evaluate(ScoringInputDto scoringInputDto) {
        if (scoringInputDto.allOverduePayments() == 0) {
            log.info("Scoring '{}': no history or no overdue, passed", getDescription());
            return APPROVE_SCORES;
        }

        double overdueRatio = (double) scoringInputDto.allDonePayments() / (double) scoringInputDto.allOverduePayments();

        return overdueRatio < MAX_OVERDUED_PAYMENTS_RATIO ? 0L : APPROVE_SCORES;
    }

    @Override
    public String getDescription() {
        return "Отсутствие просроченных платежей по предыдущим кредитам";
    }
}