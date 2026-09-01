package ru.creditbank.loan.management.enitity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Data
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

    @Column(name = "load_id", nullable = false)
    private UUID loadId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "datetime", nullable = false)
    private Instant datetime;

    @Column(name = "type", nullable = false)
    private PaymentTypeEnum type;

    @Column(name = "new_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal newBalance;

    @Column(name = "created_at", nullable = false)
    Instant createdAt;
}
