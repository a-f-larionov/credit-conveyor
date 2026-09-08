package ru.creditbank.credit.operations.controller;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;
import ru.creditbank.common.library.dto.loan.management.rs.ClientLoanPaymentsStatisticRsDto;
import ru.creditbank.common.library.enums.CreditStatusEnum;
import ru.creditbank.credit.operations.SpringBootMvcProxyBaseTest;
import ru.creditbank.credit.operations.TestJwtGenerator;
import ru.creditbank.credit.operations.service.MailOutBoxService;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.common.library.enums.CreditStatusEnum.APPROVED;
import static ru.creditbank.common.library.enums.CreditStatusEnum.REJECTED;
import static ru.creditbank.credit.operations.TestFixtures.buildCreditCreateRqDto;

@TestPropertySource(properties = {
        "spring.mail.host=localhost",
        "spring.mail.port=3025",
        "spring.mail.username=",
        "spring.mail.password=",
        "spring.mail.properties.mail.smtp.auth=false"
})
@TestPropertySource(properties = "credit.auto-decision.enabled=true")
class CreditControllerEnabledAutoDecisionTest extends SpringBootMvcProxyBaseTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @Autowired
    MailOutBoxService mailOutBoxService;

    @Autowired
    TestJwtGenerator jwtGenerator;


    static Stream<Arguments> creditTestData() {
        return Stream.of(
                of(12, 100_000, 100, 0, APPROVED),
                of(12, 100_000, 100, 80, REJECTED),
                of(12, 15_000, 100, 1, REJECTED),
                of(3, 100_1000, 100, 1, REJECTED)
        );
    }

    @ParameterizedTest
    @MethodSource("creditTestData")
    @SneakyThrows
    void createAndAutoApprove(
            int employmentMonths, long monthlyIncome, int allDonePayments, int allOverduePayments,
            CreditStatusEnum expectedStatus
    ) {
        // given
        var rqDto = buildCreditCreateRqDto(
                "Иванов Иван Иванович",
                employmentMonths,
                new BigDecimal(monthlyIncome),
                new BigDecimal(1_000_000)
        );

        var statRsDto = ClientLoanPaymentsStatisticRsDto.builder()
                .allDonePayments(allDonePayments)
                .allOverduePayments(allOverduePayments)
                .build();

        // give: static
        var userId = UUID.randomUUID();
        var userEmail = "userEmail@mail.mail";
        var token = jwtGenerator.generate(userId, userEmail);

        enqueueResponse(statRsDto, HttpStatus.OK);

        // when
        var rsDto = performPost("/credit-service/api/v1/credits/create", rqDto, CreditCreateRsDto.class, status().isOk(), token);

        // then: call to loan-management-service
        testRequest("/loan-management-service/api/v1/payments/user-statistic/" + userId, null, token, HttpMethod.GET);

        // then: answer
        assertThat(rsDto.id()).isNotNull();
        assertThat(rsDto.createdAt()).isBetween(now().minus(10, MINUTES), now());
        assertThat(rsDto.status()).isEqualTo(expectedStatus);

        // then:mail notification
        mailOutBoxService.trySendOne();
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Должно быть отправлено ровно одно письмо");
        MimeMessage message = messages[0];

        assertEquals(userEmail, message.getAllRecipients()[0].toString());
        assertEquals("Ваша кредитная заявка #" + rsDto.id(), message.getSubject());
        assertEquals("Уважаемый Иванов Иван Иванович, ваша заявка на кредит переведена в статус: " + expectedStatus.getDescription() + ".  \n",
                message.getContent().toString());

    }
}