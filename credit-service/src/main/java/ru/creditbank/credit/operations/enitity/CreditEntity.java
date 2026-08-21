package ru.creditbank.credit.operations.enitity;

import jakarta.persistence.*;
import lombok.*;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "credit")
public class CreditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_full_name", nullable = false, length = 100)
    private String userFullName;

    @Column(name = "requested_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedAmount;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CreditStatusEnum status;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

}
