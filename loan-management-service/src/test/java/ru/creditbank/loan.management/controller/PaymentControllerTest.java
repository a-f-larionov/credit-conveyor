package ru.creditbank.loan.management.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.loan.management.SpringBootMvcBaseTest;
import ru.creditbank.loan.management.TestJwtGenerator;
import ru.creditbank.loan.management.dto.rs.PaymentRsDto;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.loan.management.TestFixtures.buildPaymentsMakeRqDto;

public class PaymentControllerTest extends SpringBootMvcBaseTest {

    @Autowired
    TestJwtGenerator jwtGenerator;

    @Test
    void makePayment() {
        // given
        var rqDto = buildPaymentsMakeRqDto();
        var token = jwtGenerator.generate();

        // when
        var rsDto = performPostWithDto("/loan-management-service/api/v1/payments/make", rqDto, PaymentRsDto.class, status().isOk(), token);

        // then
        assertNotNull(null, "TODO");
    }

    @Test
    void list() {
        // given
        var userId = UUID.randomUUID();
        var token = jwtGenerator.generate();

        // when
        var rsDto = performGetWithDto("/loan-management-service/api/v1/payments/history/"+userId, PaymentRsDto.class, status().isOk(), token);

        // then
        assertNotNull(null, "TODO");
    }
}
