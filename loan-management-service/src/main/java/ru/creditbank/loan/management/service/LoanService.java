package ru.creditbank.loan.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.common.library.service.SecurityService;
import ru.creditbank.loan.management.dto.rq.CreateLoanRqDto;
import ru.creditbank.loan.management.dto.rs.LoanListRsDto;
import ru.creditbank.loan.management.dto.rs.LoanRsDto;
import ru.creditbank.loan.management.exception.LoanNotFoundException;
import ru.creditbank.loan.management.mappers.LoanMapper;
import ru.creditbank.loan.management.repository.LoanRepository;

import java.util.UUID;

import static ru.creditbank.common.library.enums.UserRole.ROLE_ADMIN;
import static ru.creditbank.common.library.enums.UserRole.ROLE_CREDIT_MANAGER;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanService {

    private final SecurityService securityService;
    private final PaymentScheduleGeneratorService paymentScheduleGeneratorService;
    private final LoanMapper loanMapper;
    private final LoanRepository loanRepository;

    @Transactional
    public LoanRsDto create(CreateLoanRqDto rqDto) {
        log.info("Creating loan for user: {}", rqDto.userId());
        var loanEntity = loanMapper.toEntityForCreate(rqDto);

        var schedulePayments = paymentScheduleGeneratorService.generateSchedulePayments(
                rqDto.firstPaymentDate(),
                rqDto.termMonths(),
                rqDto.totalAmount(),
                rqDto.interestRate(),
                loanEntity);

        loanEntity.setSchedulePayments(schedulePayments);

        loanRepository.save(loanEntity);

        return loanMapper.entityToRsDto(loanEntity);
    }

    @Transactional(readOnly = true)
    public LoanRsDto info(UUID loanId) {
        log.info("Fetching loan by id: {}", loanId);
        var loanEntity = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        securityService.checkAccess(loanEntity.getUserId(), ROLE_CREDIT_MANAGER, ROLE_ADMIN);
        return loanMapper.entityToRsDto(loanEntity);
    }

    @Transactional(readOnly = true)
    public LoanListRsDto list(UUID userId) {
        log.info("Fetching loans by userId: {}", userId);
        securityService.checkAccess(userId, ROLE_ADMIN, ROLE_CREDIT_MANAGER);

        var entities = loanRepository.findByUserId(userId);
        var rsDtoList = loanMapper.mapToLoanList(entities);

        return loanMapper.toLoanListRsDto(rsDtoList);
    }
}
