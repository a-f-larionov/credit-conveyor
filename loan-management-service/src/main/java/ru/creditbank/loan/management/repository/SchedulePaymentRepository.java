package ru.creditbank.loan.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.creditbank.loan.management.enitity.SchedulePaymentEntity;
import ru.creditbank.loan.management.enums.PaymentStatusEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SchedulePaymentRepository extends JpaRepository<SchedulePaymentEntity, UUID> {

    Optional<SchedulePaymentEntity> findFirstByLoanIdAndStatusInOrderByDateAsc(UUID loanId, List<PaymentStatusEnum> status);

    List<SchedulePaymentEntity> findAllByLoanIdAndStatusInOrderByDateAsc(UUID loanId, List<PaymentStatusEnum> status);
}
