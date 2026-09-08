package ru.creditbank.loan.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.common.library.enums.SchedulePaymentStatusEnum;
import ru.creditbank.loan.management.enitity.SchedulePaymentEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface SchedulePaymentRepository extends JpaRepository<SchedulePaymentEntity, UUID> {

    Optional<SchedulePaymentEntity> findFirstByLoanIdAndStatusInOrderByDateAsc(UUID loanId, List<SchedulePaymentStatusEnum> status);

    List<SchedulePaymentEntity> findAllByLoanIdAndStatusInOrderByDateAsc(UUID loanId, List<SchedulePaymentStatusEnum> status);

    Integer countByUserIdAndStatus(UUID userId, SchedulePaymentStatusEnum status);

    Integer countByUserIdAndOverdueDaysIsGreaterThan(UUID userId,  Long overdueDays);

    List<SchedulePaymentEntity> findByStatusNotInAndDateBefore(Set<SchedulePaymentStatusEnum> done, Instant now);
}
