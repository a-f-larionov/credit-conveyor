package ru.creditbank.credit.operations.decision.rule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.ClientLoanPaymentsStatisticRsDto;

import java.util.UUID;

import static ru.creditbank.credit.operations.service.DecisionService.APPROVE_SCORES;

@Component
@Slf4j
public class PaymentHistoryScoreRule implements CreditScoreRule {

    private static final double MAX_OVERDUED_PAYMENTS_RATIO = 2;

    @Override
    public Long evaluate(UUID creditId, CreditCreateRqDto request, ClientLoanPaymentsStatisticRsDto statistic) {
        if (statistic == null || statistic.allOverduePayments() <= 0) {
            log.debug("Scoring '{}': no history or no overdue, passed", getDescription());
            return APPROVE_SCORES;
        }

        double overdueRation = (double) statistic.allDonePayments() / statistic.allOverduePayments();

        return overdueRation < MAX_OVERDUED_PAYMENTS_RATIO ? 0L : APPROVE_SCORES;
    }

    @Override
    public String getDescription() {
        return "Отсутствие просроченных платежей по предыдущим кредитам";
    }
}