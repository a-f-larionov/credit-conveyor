package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.credit.operations.enitity.MailOutboxEntity;
import ru.creditbank.credit.operations.repository.MailOutboxRepository;

import java.time.Instant;

import static ru.creditbank.credit.operations.enums.EmailOutboxStatusEnum.*;

@Service
@RequiredArgsConstructor
public class MailOutBoxService {

    private final MailService mailService;
    private final MailOutboxRepository mailOutboxRepository;

    @Transactional
    public void queue(String to, String subject, String body) {

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
        mailOutboxRepository.findOneNew()
                .ifPresent(this::sendNow);
    }

    private void sendNow(MailOutboxEntity email) {

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
