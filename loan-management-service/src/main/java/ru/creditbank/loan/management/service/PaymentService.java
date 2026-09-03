package ru.creditbank.loan.management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.common.library.service.SecurityService;
import ru.creditbank.loan.management.dto.rq.PaymentRqDto;
import ru.creditbank.loan.management.dto.rs.PaymentHistoryItemRsDto;
import ru.creditbank.loan.management.dto.rs.PaymentHistoryRsDto;
import ru.creditbank.loan.management.dto.rs.PaymentRsDto;
import ru.creditbank.loan.management.enitity.LoanEntity;
import ru.creditbank.loan.management.enitity.PaymentEntity;
import ru.creditbank.loan.management.enitity.SchedulePaymentEntity;
import ru.creditbank.loan.management.enums.LoanStatusEnum;
import ru.creditbank.loan.management.enums.PaymentStatusEnum;
import ru.creditbank.loan.management.exception.LoanNotFoundException;
import ru.creditbank.loan.management.exception.PaymentException;
import ru.creditbank.loan.management.mappers.PaymentMapper;
import ru.creditbank.loan.management.repository.LoanRepository;
import ru.creditbank.loan.management.repository.PaymentRepository;
import ru.creditbank.loan.management.repository.SchedulePaymentRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static ru.creditbank.common.library.enums.UserRole.ROLE_CREDIT_MANAGER;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final SecurityService securityService;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;

    private final LoanRepository loanRepository;
    private final SchedulePaymentRepository schedulePaymentRepository;

    @Transactional
    public PaymentRsDto processPayment(PaymentRqDto rqDto) {
        log.info("Processing payment: loanId={}, amount={}, type={}",
                rqDto.loanId(), rqDto.amount(), rqDto.type());

        var loan = loanRepository.findById(rqDto.loanId())
                .orElseThrow(() -> new LoanNotFoundException(rqDto.loanId()));

        securityService.checkAccess(loan.getUserId());
        validateLoanStatus(loan);

        processPaymentByType(rqDto, loan);
        updateNextPaymentDate(loan);
        var paymentEntity = savePaymentToHistory(rqDto, loan.getRemainingAmount());

        log.info("Payment processed successfully paymentId={}, remainingAmount={}, nextPaymentDate={}",
                paymentEntity.getId(), loan.getRemainingAmount(), loan.getNextPaymentDate());

        return paymentMapper.toRsDto(
                paymentEntity,
                loan.getNextPaymentDate(),
                loan.getRemainingAmount()
        );
    }

    @Transactional(readOnly = true)
    public PaymentHistoryRsDto history(UUID loanId) {
        log.info("Fetching payment history for loan: {}", loanId);

        var loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        securityService.checkAccess(loan.getUserId(), ROLE_CREDIT_MANAGER);

        List<PaymentHistoryItemRsDto> historyRsDtoList = paymentMapper.mapToList(loan.getPayments());

        return paymentMapper.toPaymentHistoryRsDto(historyRsDtoList);
    }

    private static void validateLoanStatus(LoanEntity loan) {
        if (loan.getStatus().equals(LoanStatusEnum.CLOSED)) {
            log.warn("Attempt to pay closed loan: {}", loan.getId());
            throw new PaymentException(format("Loan with id: %s is closed", loan.getId()), BAD_REQUEST);
        }
    }

    private void updateNextPaymentDate(LoanEntity loan) {
        var nextPayment = schedulePaymentRepository.findFirstByLoanIdAndStatusInOrderByDateAsc(
                        loan.getId(), List.of(PaymentStatusEnum.PENDING, PaymentStatusEnum.OVERDUE))
                .orElse(null);

        if (nextPayment == null) {
            log.info("No pending payments left for loanId={}, status CLOSED now", loan.getId());
            loan.setNextPaymentDate(null);
            loan.setStatus(LoanStatusEnum.CLOSED);
        } else {
            log.debug("update next payment date for loanId={} date={}", loan.getId(), nextPayment.getDate());
            loan.setNextPaymentDate(nextPayment.getDate());
        }
    }

    @NonNull
    private PaymentEntity savePaymentToHistory(PaymentRqDto rqDto, BigDecimal remainingAmount) {
        log.debug("Save payment loanId={}, amount={}, remaining={}",
                rqDto.loanId(), rqDto.amount(), remainingAmount);

        var paymentEntity = paymentMapper.toEntityForCreate(rqDto, remainingAmount, Instant.now());
        paymentRepository.save(paymentEntity);
        return paymentEntity;
    }

    private void processPaymentByType(PaymentRqDto rqDto, LoanEntity loan) {
        switch (rqDto.type()) {
            case REGULAR -> processRegularPayment(loan, rqDto);
            case EARLY -> processEarlyPayment(loan, rqDto);
            case PARTIAL -> processPartialPayment(loan, rqDto);
            case FULL -> processFullPayment(loan, rqDto);
        }
    }

    private void processRegularPayment(LoanEntity loan, PaymentRqDto rqDto) {
        log.debug("Process REGULAR payment for loan={}", loan.getId());
        var payment = schedulePaymentRepository.findFirstByLoanIdAndStatusInOrderByDateAsc(
                        loan.getId(), List.of(PaymentStatusEnum.PENDING, PaymentStatusEnum.OVERDUE))
                .orElseThrow(() -> new IllegalStateException(format("No payments found for %s", rqDto.loanId())));

        var totalAmount = payment.getInterestAmount().add(payment.getPrincipalAmount());
        if (totalAmount.compareTo(rqDto.amount()) != 0) {
            throw new PaymentException("Amount " + rqDto.amount() + " mismatch nearest scheduled payment "
                    + totalAmount, BAD_REQUEST);
        }

        payment.setStatus(PaymentStatusEnum.DONE);
        payment.setDoneDate(Instant.now());

        loan.setRemainingAmount(payment.getRemainAmount());

        log.info("REGULAR payment processed: paymentId={}, newRemaining={}",
                payment.getId(), payment.getRemainAmount());
    }

    private void processFullPayment(LoanEntity loan, PaymentRqDto rqDto) {
        log.debug("Process FULL payment for loan {}", loan.getId());
        List<SchedulePaymentEntity> remainingPayments = schedulePaymentRepository
                .findAllByLoanIdAndStatusInOrderByDateAsc(
                        loan.getId(),
                        List.of(PaymentStatusEnum.PENDING, PaymentStatusEnum.OVERDUE)
                );

        if (remainingPayments.isEmpty()) {
            throw new IllegalStateException(format("No pending payments, but loan is not closed for id %s", loan.getId()));
        }

        var totalRemaining = remainingPayments.stream()
                .map(p -> p.getInterestAmount().add(p.getPrincipalAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (rqDto.amount().compareTo(totalRemaining) != 0) {
            throw new PaymentException(
                    String.format("Full payment amount must be equal to %s, but given %s", totalRemaining, rqDto.amount()),
                    BAD_REQUEST);
        }

        var now = Instant.now();
        for (var payment : remainingPayments) {
            payment.setStatus(PaymentStatusEnum.DONE);
            payment.setDoneDate(now);
        }

        loan.setRemainingAmount(BigDecimal.ZERO);

        log.info("Full payment completed for loan {}, all {} payments marked as DONE",
                loan.getId(), remainingPayments.size());
    }

    private void processPartialPayment(LoanEntity loan, PaymentRqDto rqDto) {
        log.warn("PARTIAL payment not yet implemented for loan {}", loan.getId());
        throw new PaymentException("Not supported type", BAD_REQUEST);
    }

    private void processEarlyPayment(LoanEntity loan, PaymentRqDto rqDto) {
        log.warn("EARLY payment not yet implemented for loan {}", loan.getId());
        throw new PaymentException("Not supported type", BAD_REQUEST);
    }
}
