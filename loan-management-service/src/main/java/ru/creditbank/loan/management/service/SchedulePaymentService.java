package ru.creditbank.loan.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.common.library.service.SecurityService;
import ru.creditbank.loan.management.dto.rs.LoanPaymentsScheduleListRsDto;
import ru.creditbank.loan.management.exception.LoanNotFoundException;
import ru.creditbank.loan.management.mappers.LoanMapper;
import ru.creditbank.loan.management.repository.LoanRepository;

import java.util.UUID;

import static ru.creditbank.common.library.enums.UserRole.ROLE_ADMIN;
import static ru.creditbank.common.library.enums.UserRole.ROLE_CREDIT_MANAGER;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulePaymentService {

    private final SecurityService securityService;
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;

    @Transactional(readOnly = true)
    public LoanPaymentsScheduleListRsDto list(UUID loanId) {
        log.info("Fetching payments schedule by loanId: {}", loanId);

        var loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        securityService.checkAccess(loan.getUserId(), ROLE_CREDIT_MANAGER, ROLE_ADMIN);

        var list = loanMapper.mapToPaymentScheduleList(loan.getSchedulePayments());

        return loanMapper.toLoanPaymentsScheduleListRsDto(loanId, list);
    }
}
