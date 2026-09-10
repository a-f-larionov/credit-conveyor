package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;
import ru.creditbank.common.library.service.SecurityService;
import ru.creditbank.credit.operations.mappers.CreditMapper;
import ru.creditbank.credit.operations.repository.CreditRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditSyncOrchestratorService {

    private final SecurityService securityService;
    private final CreditService creditService;
    private final ScoreService scoreService;
    private final AutoDecisionService autoDecisionService;
    private final CreditMapper creditMapper;
    private final CreditRepository creditRepository;

    @Transactional
    public CreditCreateRsDto createAndProcess(CreditCreateRqDto rqDto) {

        // Stage 1: create credit request
        var createRsDto = creditService.create(rqDto);

        // Stage 2: prepare credit scoring
        var userId = securityService.getUserDetails().getId();
        var stat = scoreService.prepareCreditScoring(userId, createRsDto.id());

        // Stage 3: process credit scoring
        scoreService.processCreditScoring(createRsDto.id(), stat);

        // Stage 4: process auto decision
        autoDecisionService.processAutoDecision(createRsDto.id());

        // stage 5: build answer
        var credit = creditRepository.findById(createRsDto.id())
                .orElseThrow(IllegalStateException::new);
        return creditMapper.mapEntityToCreateRsDto(credit);
    }
}
