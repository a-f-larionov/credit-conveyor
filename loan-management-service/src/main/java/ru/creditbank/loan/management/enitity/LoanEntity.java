package ru.creditbank.loan.management.enitity;

import jakarta.persistence.*;
import lombok.*;
import ru.creditbank.loan.management.enums.LoanStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
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

    @Column(name = "total_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "remaining_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal remainingAmount;

    @Column(name = "next_payment_date", nullable = false)
    private Instant nextPaymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private LoanStatusEnum status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
