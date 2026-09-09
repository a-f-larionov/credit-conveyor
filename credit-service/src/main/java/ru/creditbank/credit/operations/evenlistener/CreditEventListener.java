package ru.creditbank.credit.operations.evenlistener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
    @Retryable(retryFor = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    @EventListener
    public void handleCreditCreated(CreditCreatedEvent event) {
        log.info("Event raise {} userId={} creditId={}", event.getClass().getName(), event.getUserId(), event.getCreditId());
        try {
            scoreService.prepareCreditScoring(event.getUserId(), event.getCreditId());
        } catch (Exception e) {
            log.error("Event {} failed for userId={} creditId={}", event.getClass().getName(), event.getUserId(), event.getCreditId(), e);
        }
    }

    @Async
    @Retryable(retryFor = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    @EventListener
    public void handleScoringPrepared(ScoringPreparedEvent event) {
        log.info("Raise event {} creditId={} statistic={}", event.getClass().getName(), event.getCreditId(), event.getStatisticRsDto());
        try {
            scoreService.processCreditScoring(event.getCreditId(), event.getStatisticRsDto());
        } catch (Exception e) {
            log.error("Failed event {} creditId={} statistic={}", event.getClass().getName(), event.getCreditId(), event.getStatisticRsDto(), e);
        }
    }

    @Async
    @Retryable(retryFor = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000, multiplier = 2))
    @EventListener
    public void handleScoreCreditUpdatedEvent(ScoreCreditUpdatedEvent event) {
        log.info("Raise event {} creditId={}", event.getClass().getName(), event.getCreditId());
        try {
            autoDecisionService.processAutoDecision(event.getCreditId());
        } catch (Exception e) {
            log.error("Failed event {} creditId={} ", event.getClass().getName(), event.getCreditId(), e);
        }
    }
}