package ru.creditbank.credit.operations.evenlistener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.creditbank.credit.operations.event.CreditCreatedEvent;
import ru.creditbank.credit.operations.event.ScoreCreditUpdatedEvent;
import ru.creditbank.credit.operations.event.ScoringPreparedEvent;
import ru.creditbank.credit.operations.service.AutoDecisionService;
import ru.creditbank.credit.operations.service.ScoreService;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditEventListener {

    private final ScoreService scoreService;
    private final AutoDecisionService autoDecisionService;

    @Async
    @EventListener
    public void handleCreditCreated(CreditCreatedEvent event) {
        log.info("Event raise {} userId={} creditId={}", event.getClass().getName(), event.userId(), event.creditId());
        try {
            scoreService.prepareCreditScoring(event.userId(), event.creditId());
        } catch (Exception e) {
            log.error("Event {} failed for userId={} creditId={}", event.getClass().getName(), event.userId(), event.creditId(), e);
        }
    }

    @Async
    @EventListener
    public void handleScoringPrepared(ScoringPreparedEvent event) {
        log.info("Raise event {} creditId={} statistic={}", event.getClass().getName(), event.creditId(), event.statisticRsDto());
        try {
            scoreService.processCreditScoring(event.creditId(), event.statisticRsDto());
        } catch (Exception e) {
            log.error("Failed event {} creditId={} statistic={}", event.getClass().getName(), event.creditId(), event.statisticRsDto(), e);
        }
    }

    @Async
    @EventListener
    public void handleScoreCreditUpdatedEvent(ScoreCreditUpdatedEvent event) {
        log.info("Raise event {} creditId={}", event.getClass().getName(), event.creditId());
        try {
            autoDecisionService.processAutoDecision(event.creditId());
        } catch (Exception e) {
            log.error("Failed event {} creditId={} ", event.getClass().getName(), event.creditId(), e);
        }
    }
}