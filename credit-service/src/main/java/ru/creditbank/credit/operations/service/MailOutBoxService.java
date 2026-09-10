package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.credit.operations.enitity.MailOutboxEntity;
import ru.creditbank.credit.operations.repository.MailOutboxRepository;

import java.time.Instant;

import static ru.creditbank.credit.operations.enums.EmailOutboxStatusEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailOutBoxService {

    private final MailService mailService;
    private final MailOutboxRepository mailOutboxRepository;

    @Transactional
    public void enqueue(String to, String subject, String body) {
        log.info("Enqueue mail: to={}, subject={}, body={}", to, subject, body);
        var emailEntity = MailOutboxEntity.builder()
                .recipient(to)
                .subject(subject)
                .body(body)
                .status(NEW)
                .create(Instant.now())
                .build();

        mailOutboxRepository.save(emailEntity);
    }

    @Scheduled(cron = "0 */1 * * * ?")
    @Transactional
    public void trySendOne() {
        log.info("Try to send emails");
        mailOutboxRepository
                .findOneNew()
                .ifPresentOrElse(
                        this::sendNow,
                        () -> log.info("No emails found to send.")
                );
    }

    private void sendNow(MailOutboxEntity email) {
        log.info("Try send mail with id: {}", email.getId());

        try {
            mailService.sendSimpleMessage(email.getRecipient(), email.getSubject(), email.getBody());

            email.setSentAt(Instant.now());
            email.setStatus(SENT);
            mailOutboxRepository.save(email);

        } catch (Exception e) {

            email.setStatus(FAILURE);
            email.setLastError(e.getMessage());

            mailOutboxRepository.save(email);
        }
    }
}
