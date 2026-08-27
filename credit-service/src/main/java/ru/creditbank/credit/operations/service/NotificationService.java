package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ru.creditbank.credit.operations.enitity.CreditEntity;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MailService mailService;

    public void onCreditStatusChange(CreditEntity creditEntity) {

        var to = creditEntity.getUserEmail();
        var subject = format("Ваша кредитная заявка #%s", creditEntity.getId());

        var body = getMailBody(creditEntity);

        mailService.sendSimpleMessage(to, subject, body);
    }

    @NonNull
    private String getMailBody(CreditEntity creditEntity) {
        var bodyBuilder = new StringBuilder();
        bodyBuilder.append(format("Уважаемый %s, ваша заявка на кредит переведена в статус: %s.  \n",
                creditEntity.getUserFullName(),
                creditEntity.getStatus())
        );

        if (creditEntity.getManagerComment() != null) {
            bodyBuilder.append(format("Комментарий менеджера: %s", creditEntity.getManagerComment()));
        }
        return bodyBuilder.toString();
    }
}
