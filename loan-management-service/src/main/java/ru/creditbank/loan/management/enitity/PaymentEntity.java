package ru.creditbank.loan.management.enitity;

import jakarta.persistence.*;
import lombok.*;
import ru.creditbank.loan.management.enums.PaymentTypeEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Builder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @JoinColumn(name = "loan_id", nullable = false)
    @ManyToOne(fetch = LAZY)
    private LoanEntity loan;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "remaining", nullable = false, precision = 15, scale = 2)
    private BigDecimal remaining;

    @Column(name = "type", nullable = false)
    @Enumerated(STRING)
    private PaymentTypeEnum type;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
