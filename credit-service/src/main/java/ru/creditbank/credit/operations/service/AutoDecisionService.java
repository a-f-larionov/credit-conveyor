package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.credit.operations.exception.AutoDecisionException;
import ru.creditbank.credit.operations.exception.CreditNotFoundException;
import ru.creditbank.credit.operations.mappers.CreditMapper;
import ru.creditbank.credit.operations.repository.CreditRepository;

import java.util.UUID;

import static java.lang.String.format;
import static ru.creditbank.common.library.enums.CreditStatusEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutoDecisionService {

    public static final long APPROVE_SCORES = 100L;
    private static final String DEFAULT_MANAGER_COMMENT = "Сработало авто принятие решения.";

    @Value("${credit.auto-decision.enabled:false}")
    private boolean isEnabled;

    private final CreditService creditService;
    private final CreditMapper creditMapper;
    private final CreditRepository creditRepository;

    @Transactional
    public void processAutoDecision(UUID creditId) {
        if (isEnabled) {
            log.info("Auto decision: creditId={}", creditId);
            doDecide(creditId);
        } else {
            log.warn("Auto decision service is disabled. Skip: creditId={}", creditId);
        }
    }

    private void doDecide(UUID creditId) {
        var creditEntity = creditRepository.findById(creditId)
                .orElseThrow(() -> new CreditNotFoundException(creditId));

        if (creditEntity.getStatus() != PENDING) {
            throw new AutoDecisionException(format("Credit %s request must be in status %s but status %s", creditId, PENDING, creditEntity.getStatus()));
        }
        if (creditEntity.getScore() == null) {
            throw new AutoDecisionException(format("Credit %s request must be scored.", creditEntity.getId()));
        }

        var decidedStatus = creditEntity.getScore() >= APPROVE_SCORES ? APPROVED : REJECTED;

        creditService.statusUpdate(creditId, creditMapper.toStatusUpdateRqDto(decidedStatus, DEFAULT_MANAGER_COMMENT));
    }
}

