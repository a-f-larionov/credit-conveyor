package ru.creditbank.credit.operations.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.credit.operations.SpringBootMvcBaseTest;
import ru.creditbank.credit.operations.TestFixtures;
import ru.creditbank.credit.operations.TestJwtGenerator;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreditCreateControllerTest extends SpringBootMvcBaseTest {

    @Autowired
    TestJwtGenerator jwtGenerator;

    @Test
    void createNoToken() throws Exception {
        // given
        var rqDto = TestFixtures.buildCreditCreateRqDto();

        // when - then
        performPostWithDto("/credit-service/api/v1/create", rqDto, CreditCreateRsDto.class, status().isForbidden());
    }

    @Test
    void createInvalidToken() throws Exception {
        // given
        var rqDto = TestFixtures.buildCreditCreateRqDto();
        var token = "invalid-token";

        // when - then
        performPostWithDto("/credit-service/api/v1/create", rqDto, CreditCreateRsDto.class, status().isForbidden(), token);
    }

    @Test
    void create() throws Exception {
        // given
        var rqDto = TestFixtures.buildCreditCreateRqDto();
        var token = jwtGenerator.generateDefault();

        // when
        var rsDto = performPostWithDto("/credit-service/api/v1/create", rqDto, CreditCreateRsDto.class, status().isOk(), token);

        // then
        assertThat(rsDto.id()).isNotNull();
        assertThat(rsDto.createAt()).isBetween(now().minus(10, MINUTES), now());
        assertThat(rsDto.status()).isEqualTo(CreditStatusEnum.APPROVED);
    }
}