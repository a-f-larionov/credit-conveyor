package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.common.library.client.LoanManagementPaymentsServiceClient;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.ClientLoanPaymentsStatisticRsDto;
import ru.creditbank.credit.operations.decision.rule.CreditScoreRule;
import ru.creditbank.credit.operations.exception.CreditNotFoundException;
import ru.creditbank.credit.operations.mappers.CreditMapper;
import ru.creditbank.credit.operations.repository.CreditRepository;

import java.util.List;
import java.util.UUID;

import static ru.creditbank.common.library.enums.CreditStatusEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionService {

    public static final long APPROVE_SCORES = 100L;

    @Value("${credit.auto-decision.enabled:false}")
    private boolean isEnabled;

    private final List<CreditScoreRule> creditScoreRuleList;

    private final LoanManagementPaymentsServiceClient loanManagementPaymentsServiceClient;
    private final CreditService creditService;
    private final CreditMapper creditMapper;
    private final CreditRepository creditRepository;

    @Transactional
    public void decide(UUID creditId) {
        if (isEnabled) {
            log.info("Decide about creditId: {}", creditId);
            doDecide(creditId);
        } else {
            log.info("Service is disabled. Skip decide about creditId: {}", creditId);
        }
    }

    private void doDecide(UUID creditId) {

        var credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new CreditNotFoundException(creditId));

        if (!credit.getStatus().equals(PENDING)) {
            log.info("Credit {} request must in status {} but status {}", creditId, PENDING, credit.getStatus());
            return;
        }

        var statistic =
                loanManagementPaymentsServiceClient.userStatistic(credit.getUserId());

        var creditRqDto = creditMapper.mapEntityToCreateRqDto(credit);


        var score = getScoreFor(creditId, creditRqDto, statistic);

        log.info("Total score: creditId={} score={}", creditId, score);
        var statusToAprovedRqDto = StatusUpdateRqDto.builder()
                .status(score >= APPROVE_SCORES ? APPROVED : REJECTED)
                .build();

        creditService.statusUpdate(statusToAprovedRqDto, creditId);
    }

    private long getScoreFor(UUID creditId, CreditCreateRqDto creditRqDto, ClientLoanPaymentsStatisticRsDto statistic) {
        if (creditScoreRuleList.isEmpty()) {
            return APPROVE_SCORES;
        }
        var sumScore = 0L;
        for (var creditScoreRule : creditScoreRuleList) {
            var score = creditScoreRule.evaluate(creditId, creditRqDto, statistic);
            sumScore += score;
            log.info("Score {} for: {} creditId={}, {}, {}",
                    score, creditScoreRule.getDescription(), creditScoreRuleList, creditRqDto, statistic);
        }
        return sumScore / creditScoreRuleList.size();
    }
}

