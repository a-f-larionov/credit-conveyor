package ru.creditbank.loan.management.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.common.library.enums.SchedulePaymentStatusEnum;
import ru.creditbank.loan.management.enitity.SchedulePaymentEntity;
import ru.creditbank.loan.management.repository.SchedulePaymentRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class OverdueScheduleService {

    private final SchedulePaymentRepository schedulePaymentRepository;

    @Scheduled(cron = "0 0 0 * * ?") // каждый день в 00:00:00
    @Transactional
    public void checkOverdueSchedulePayments() {
        log.info("Check overdue schedule payments");

        var schedulePayments = schedulePaymentRepository
                .findByStatusNotInAndDateBefore(Set.of(SchedulePaymentStatusEnum.DONE), Instant.now());

        log.info("Check overdue schedule payments, find overdue payments {}", schedulePayments.size());
        schedulePayments.forEach(this::checkOverdueSchedulePayments);
    }

    private void checkOverdueSchedulePayments(SchedulePaymentEntity schedulePaymentEntity) {
        log.info("Check overdue schedule payment. Update date for paymentId:{}", schedulePaymentEntity.getId());
        var overdueDays = ChronoUnit.DAYS.between(
                schedulePaymentEntity.getDate(), Instant.now()
        );
        schedulePaymentEntity.setOverdueDays(overdueDays);
    }
}