package ru.creditbank.credit.operations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.creditbank.credit.operations.enitity.MailOutboxEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MailOutboxRepository extends JpaRepository<MailOutboxEntity, UUID> {

    @Query(value = "SELECT * FROM email_outbox WHERE status = 'NEW' LIMIT 1", nativeQuery = true)
    Optional<MailOutboxEntity> findOneNew();
}
