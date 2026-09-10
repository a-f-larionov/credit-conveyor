package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.common.library.client.LoanManagementPaymentsServiceClient;
import ru.creditbank.common.library.dto.loan.management.rs.UserLoanPaymentsStatisticRsDto;
import ru.creditbank.credit.operations.decision.rule.CreditScoreRule;
import ru.creditbank.credit.operations.dto.ScoringInputDto;
import ru.creditbank.credit.operations.event.ScoreCreditUpdatedEvent;
import ru.creditbank.credit.operations.event.ScoringPreparedEvent;
import ru.creditbank.credit.operations.exception.CreditNotFoundException;
import ru.creditbank.credit.operations.mappers.CreditScoreMapper;
import ru.creditbank.credit.operations.repository.CreditRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreService {

    private final List<CreditScoreRule> creditScoreRuleList;

    private final LoanManagementPaymentsServiceClient loanManagementPaymentsServiceClient;
    private final CreditScoreMapper creditScoreMapper;
    private final CreditRepository creditRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void prepareCreditScoring(UUID userId, UUID creditId) {
        log.info("Prepare credit scoring: userId={} creditId={}", userId, creditId);
        var stat = loanManagementPaymentsServiceClient.userStatistic(userId);
        applicationEventPublisher.publishEvent(new ScoringPreparedEvent(creditId, stat));
    }

    @Transactional
    public void processCreditScoring(UUID creditId, UserLoanPaymentsStatisticRsDto statistic) {
        log.info("Process credit scoring: creditId={} statistic={}", creditId, statistic);
        var creditEntity = creditRepository.findById(creditId)
                .orElseThrow(() -> new CreditNotFoundException(creditId));

        var scoringDto = creditScoreMapper.toScoringInputDto(creditEntity, statistic);
        var score = calculateScore(scoringDto);
        creditEntity.setScore(score);
        creditRepository.save(creditEntity);

        applicationEventPublisher.publishEvent(new ScoreCreditUpdatedEvent(scoringDto.creditId()));
    }

    private Long calculateScore(ScoringInputDto scoringInputDto) {
        if (creditScoreRuleList.isEmpty()) {
            throw new IllegalArgumentException("No credit score rules. Disable auto decision or add some rules");
        }
        Long sumScore = 0L;

        for (var creditScoreRule : creditScoreRuleList) {
            var score = creditScoreRule.evaluate(scoringInputDto);
            sumScore += score;
            log.info("Score {} for: {} creditId={}, userId={}",
                    score, creditScoreRule.getDescription(),
                    scoringInputDto.creditId(), scoringInputDto.userId());
        }
        return sumScore / creditScoreRuleList.size();
    }
}
