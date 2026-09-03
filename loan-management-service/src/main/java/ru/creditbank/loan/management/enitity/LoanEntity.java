package ru.creditbank.loan.management.enitity;

import jakarta.persistence.*;
import lombok.*;
import ru.creditbank.loan.management.enums.LoanStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "loans")
public class LoanEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToMany(mappedBy = "loan", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    private List<SchedulePaymentEntity> schedulePayments = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = ALL, orphanRemoval = true, fetch = LAZY)
    private List<PaymentEntity> payments = new ArrayList<>();

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "remaining_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingAmount;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "interest_rate", nullable = false, precision = 15, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "next_payment_date")
    private Instant nextPaymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private LoanStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
