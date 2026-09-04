package ru.creditbank.credit.operations.controller;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import ru.creditbank.common.library.dto.common.rs.ErrorRsDto;
import ru.creditbank.credit.operations.SpringBootMvcBaseTest;
import ru.creditbank.credit.operations.TestFixtures;
import ru.creditbank.credit.operations.TestJwtGenerator;
import ru.creditbank.common.library.enums.CreditStatusEnum;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;
import ru.creditbank.credit.operations.service.MailOutBoxService;

import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.credit.operations.TestFixtures.buildStatusUpdateRqDto;
import static ru.creditbank.credit.operations.enums.UserRole.ROLE_CREDIT_MANAGER;
import static ru.creditbank.credit.operations.enums.UserRole.ROLE_USER;

@TestPropertySource(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=",
        "spring.mail.password=",
        "spring.mail.properties.mail.smtp.auth=false"
})
class CreditControllerUpdateStatusTest extends SpringBootMvcBaseTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @Autowired
    MailOutBoxService mailOutBoxService;

    @Autowired
    TestJwtGenerator jwtGenerator;

    @Test
    void updateStatusUnauthorizedNotToken() {
        // given
        String token = null;
        var creditId = UUID.randomUUID();

        // when
        var changeStatusRqDto = buildStatusUpdateRqDto(CreditStatusEnum.APPROVED, null);
        var rsDto = performPath("/credit-service/api/v1/status/update/" + creditId, changeStatusRqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token is empty", rsDto.message());
    }

    @Test
    void updateStatusUnauthorizedIvalidToken() {
        // given
        String token = "invalid-token";
        var creditId = UUID.randomUUID();

        // when
        var changeStatusRqDto = buildStatusUpdateRqDto(CreditStatusEnum.APPROVED, null);
        var rsDto = performPath("/credit-service/api/v1/status/update/" + creditId, changeStatusRqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    void updateStatusInvalidData() {
        // given
        String token = "invalid-token";
        var creditId = UUID.randomUUID();

        // when
        var changeStatusRqDto = buildStatusUpdateRqDto(CreditStatusEnum.APPROVED, null);
        var rsDto = performPath("/credit-service/api/v1/status/update/" + creditId, changeStatusRqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    void updateStatusCreditNotFound() {
        // given
        var userId = UUID.randomUUID();
        var userEmail = "userEmail@mail.com";
        var token = jwtGenerator.generate(userId, userEmail, Set.of(ROLE_CREDIT_MANAGER));
        var creditId = UUID.randomUUID();

        // when
        var changeStatusRqDto = buildStatusUpdateRqDto(CreditStatusEnum.APPROVED, null);
        var rsDto = performPath("/credit-service/api/v1/status/update/" + creditId, changeStatusRqDto, ErrorRsDto.class, status().isNotFound(), token);

        // then
        assertEquals(format("Credit with id %s not found", creditId), rsDto.message());
    }

    @SneakyThrows
    @Test
    void updateStatusToApproved() {
        // given
        var userId = UUID.randomUUID();
        var userEmail = "userEmail123@mail.com";
        var token = jwtGenerator.generate(userId, userEmail, Set.of(ROLE_USER, ROLE_CREDIT_MANAGER));
        var createRqDto = TestFixtures.buildCreditCreateRqDto();
        var createdRsDto = performPost("/credit-service/api/v1/create", createRqDto, CreditCreateRsDto.class, status().isOk(), token);

        // when
        var changeStatusRqDto = buildStatusUpdateRqDto(CreditStatusEnum.APPROVED, "Комментарий менеджера тестовый");
        performPath("/credit-service/api/v1/status/update/" + createdRsDto.id(), changeStatusRqDto, status().isOk(), token);

        // then
        var infoRsDto = performGet("/credit-service/api/v1/info/" + createdRsDto.id(), CreditInfoRsDto.class, status().isOk(), token);
        assertThat(infoRsDto.id()).isEqualTo(createdRsDto.id());
        assertThat(infoRsDto.userInfo().userId()).isEqualTo(userId);
        assertThat(infoRsDto.userInfo().fullName()).isEqualTo(createRqDto.fullName());
        assertThat(infoRsDto.userInfo().email()).isEqualTo(userEmail);
        assertThat(infoRsDto.createdAt()).isBetween(now().minus(10, MINUTES), now());
        assertThat(infoRsDto.status()).isEqualTo(changeStatusRqDto.status());
        assertThat(infoRsDto.loanDetails().requestedAmount()).isEqualTo(createRqDto.requestedAmount());
        assertThat(infoRsDto.loanDetails().termMonths()).isEqualTo(createRqDto.termMonths());
        assertThat(infoRsDto.loanDetails().interestRate()).isNull();

        mailOutBoxService.trySendOne();

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Должно быть отправлено ровно одно письмо");
        MimeMessage message = messages[0];

        assertEquals(userEmail, message.getAllRecipients()[0].toString());
        assertEquals("Ваша кредитная заявка #" + createdRsDto.id(), message.getSubject());
        assertEquals("Уважаемый Иванов Иван Иванович, ваша заявка на кредит переведена в статус: APPROVED.  \n" +
                "Комментарий менеджера: Комментарий менеджера тестовый", message.getContent().toString());
    }

    @Test
    void updateStatusNoCreditManagerRole() {
        // given
        var userId = UUID.randomUUID();
        var userEmail = "userEmail@mail.com";
        var token = jwtGenerator.generate(userId, userEmail);
        var createRqDto = TestFixtures.buildCreditCreateRqDto();
        var createdRsDto = performPost("/credit-service/api/v1/create", createRqDto, CreditCreateRsDto.class, status().isOk(), token);

        // when
        var changeStatusRqDto = buildStatusUpdateRqDto(CreditStatusEnum.APPROVED, null);
        var rsDto = performPath("/credit-service/api/v1/status/update/" + createdRsDto.id(), changeStatusRqDto, ErrorRsDto.class, status().isForbidden(), token);

        // then
        assertEquals("Forbidden", rsDto.message());
    }

    @Test
    void updateStatusToPending() {
        // given
        var userId = UUID.randomUUID();
        var userEmail = "userEmail@mail.com";
        var token = jwtGenerator.generate(userId, userEmail, Set.of(ROLE_USER, ROLE_CREDIT_MANAGER));
        var createRqDto = TestFixtures.buildCreditCreateRqDto();
        var createdRsDto = performPost("/credit-service/api/v1/create", createRqDto, CreditCreateRsDto.class, status().isOk(), token);

        // when
        var changeStatusRqDto = buildStatusUpdateRqDto(CreditStatusEnum.PENDING, null);
        var rsDto = performPath("/credit-service/api/v1/status/update/" + createdRsDto.id(), changeStatusRqDto, ErrorRsDto.class, status().isBadRequest(), token);

        // then
        assertEquals("Target status 'PENDING' is not allowed. Allowed values: [APPROVED, REJECTED]", rsDto.message());
    }

    @Test
    void updateStatusApprovedRepeated() {
        // given
        var userId = UUID.randomUUID();
        var userEmail = "userEmail@mail.com";
        var token = jwtGenerator.generate(userId, userEmail, Set.of(ROLE_USER, ROLE_CREDIT_MANAGER));
        var createRqDto = TestFixtures.buildCreditCreateRqDto();
        var createdRsDto = performPost("/credit-service/api/v1/create", createRqDto, CreditCreateRsDto.class, status().isOk(), token);

        var changeStatusRqDto = buildStatusUpdateRqDto(CreditStatusEnum.APPROVED, null);
        performPath("/credit-service/api/v1/status/update/" + createdRsDto.id(), changeStatusRqDto, status().isOk(), token);

        // when
        changeStatusRqDto = buildStatusUpdateRqDto(CreditStatusEnum.APPROVED, null);
        var rsDto = performPath("/credit-service/api/v1/status/update/" + createdRsDto.id(), changeStatusRqDto, ErrorRsDto.class, status().isBadRequest(), token);

        // then
        assertEquals("Credit must be PENDING, but is APPROVED", rsDto.message());
    }
}