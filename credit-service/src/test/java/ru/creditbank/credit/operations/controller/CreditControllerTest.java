package ru.creditbank.credit.operations.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.credit.operations.SpringBootMvcBaseTest;
import ru.creditbank.credit.operations.TestFixtures;
import ru.creditbank.credit.operations.TestJwtGenerator;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;

import java.util.UUID;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CreditControllerTest extends SpringBootMvcBaseTest {

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
        var token = jwtGenerator.generate();

        // when
        var rsDto = performPostWithDto("/credit-service/api/v1/create", rqDto, CreditCreateRsDto.class, status().isOk(), token);

        // then
        assertThat(rsDto.id()).isNotNull();
        assertThat(rsDto.createAt()).isBetween(now().minus(10, MINUTES), now());
        assertThat(rsDto.status()).isEqualTo(CreditStatusEnum.PENDING);
    }

    @Test
    void createAndRead() throws Exception {
        // given
        var userId = UUID.randomUUID();
        var token = jwtGenerator.generate(userId);
        var rqDto = TestFixtures.buildCreditCreateRqDto();
        var createdRsDto = performPostWithDto("/credit-service/api/v1/create", rqDto, CreditCreateRsDto.class, status().isOk(), token);

        // when
        var infoRqDto = TestFixtures.buildCreditInfoRqDto(createdRsDto.id());
        var infoRsDto = performPostWithDto("/credit-service/api/v1/info", infoRqDto, CreditInfoRsDto.class, status().isOk(), token);

        // then
        assertThat(infoRsDto.id()).isNotNull();
        assertThat(infoRsDto.creationDate()).isBetween(now().minus(10, MINUTES), now());
        assertThat(infoRsDto.lastUpdated()).isBetween(now().minus(10, MINUTES), now());
        assertThat(infoRsDto.status()).isEqualTo(CreditStatusEnum.PENDING);
        assertThat(infoRsDto.userId()).isEqualTo(userId);
        assertThat(infoRsDto.userFullName()).isEqualTo(rqDto.fullName());
        assertThat(infoRsDto.requestedAmount()).isEqualTo(rqDto.requestAmount());
        assertThat(infoRsDto.termMonths()).isEqualTo(rqDto.termMonths());
    }
}