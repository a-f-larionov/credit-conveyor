package ru.creditbank.loan.management.enitity;

import jakarta.persistence.*;
import lombok.*;
import ru.creditbank.loan.management.enums.PaymentStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "schedule_payments")
public class SchedulePaymentEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @JoinColumn(name = "loan_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private LoanEntity loan;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "interest_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal interestAmount;

    @Column(name = "principal_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "remain_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainAmount;

    @Column(name = "done_date")
    private Instant doneDate;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
