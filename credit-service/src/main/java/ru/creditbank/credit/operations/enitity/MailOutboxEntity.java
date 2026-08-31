package ru.creditbank.credit.operations.enitity;

import jakarta.persistence.*;
import lombok.*;
import ru.creditbank.credit.operations.enums.EmailOutboxStatusEnum;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "email_outbox")
public class MailOutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "recipient", nullable = false, length = 254)
    private String recipient;

    @Column(name = "subject", length = 998)
    private String subject;

    @Column(name = "body")
    private String body;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private EmailOutboxStatusEnum status;

    @Column(name = "sent_at")
    private Instant sentAt;

    @Column(name = "last_error", length = 200)
    private String lastError;

    @Column(name = "created", nullable = false)
    private Instant create;
}
