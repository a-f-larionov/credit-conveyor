package ru.creditbank.credit.operations.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.creditbank.common.library.dto.rs.ErrorRsDto;
import ru.creditbank.credit.operations.SpringBootMvcBaseTest;
import ru.creditbank.credit.operations.TestJwtGenerator;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;

import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.credit.operations.TestFixtures.buildCreditCreateRqDto;
import static ru.creditbank.credit.operations.enums.UserRole.ROLE_CREDIT_MANAGER;

class CreditControllerTest extends SpringBootMvcBaseTest {

    @Autowired
    TestJwtGenerator jwtGenerator;

    @Test
    void createNoToken() {
        // given
        var rqDto = buildCreditCreateRqDto();

        // when
        var rsDto = performPost("/credit-service/api/v1/create", rqDto, ErrorRsDto.class, status().isUnauthorized());

        // then
        assertEquals("Token is empty", rsDto.message());
    }

    @Test
    void createInvalidToken() {
        // given
        var rqDto = buildCreditCreateRqDto();
        var token = "invalid-token";

        // when
        var rsDto = performPost("/credit-service/api/v1/create", rqDto, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    void create401IncorrectData() {
        // given
        var rqDto = buildCreditCreateRqDto(""); // blank fullName is invalid
        var token = jwtGenerator.generate();

        // when
        var rsDto = performPost("/credit-service/api/v1/create", rqDto, ErrorRsDto.class, status().isBadRequest(), token);

        // then
        assertEquals("fullName : must match \"^[А-Яа-яЁёA-Za-z\\s-]+$\"; fullName : must not be blank; fullName : size must be between 5 and 100", rsDto.message());
    }

    @Test
    void create() {
        // given
        var rqDto = buildCreditCreateRqDto();
        var token = jwtGenerator.generate();

        // when
        var rsDto = performPost("/credit-service/api/v1/create", rqDto, CreditCreateRsDto.class, status().isOk(), token);

        // then
        assertThat(rsDto.id()).isNotNull();
        assertThat(rsDto.createdAt()).isBetween(now().minus(10, MINUTES), now());
        assertThat(rsDto.status()).isEqualTo(CreditStatusEnum.PENDING);
    }

    @Test
    void getInfoUnauthorizedNoToken() {
        // given
        String token = null;
        var creditId = UUID.randomUUID();

        // when
        var rsDto = performGet("/credit-service/api/v1/credit/" + creditId, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token is empty", rsDto.message());
    }

    @Test
    void getInfoUnauthorizedInvalidToken() {
        // given
        var token = "invalid-token";
        var creditId = UUID.randomUUID();

        // when
        var rsDto = performGet("/credit-service/api/v1/info/" + creditId, ErrorRsDto.class, status().isUnauthorized(), token);

        // then
        assertEquals("Token invalid", rsDto.message());
    }

    @Test
    void getInfoNotFound() {
        // given
        var token = jwtGenerator.generate();
        var creditIdNotFound = UUID.randomUUID();

        // when
        var rsDto = performGet("/credit-service/api/v1/info/" + creditIdNotFound, ErrorRsDto.class, status().isNotFound(), token);

        // then
        assertEquals(format("Credit with id %s not found", creditIdNotFound), rsDto.message());
    }

    @Test
    void getInfoByOwner() {
        // given
        var userId = UUID.randomUUID();
        var userEmail = "userEmail@mail.com";
        var token = jwtGenerator.generate(userId, userEmail);
        var createRqDto = buildCreditCreateRqDto();
        var createdRsDto = performPost("/credit-service/api/v1/create", createRqDto, CreditCreateRsDto.class, status().isOk(), token);

        // when
        var infoRsDto = performGet("/credit-service/api/v1/info/" + createdRsDto.id(), CreditInfoRsDto.class, status().isOk(), token);

        // then
        assertThat(infoRsDto.id()).isEqualTo(createdRsDto.id());
        assertThat(infoRsDto.userInfo().userId()).isEqualTo(userId);
        assertThat(infoRsDto.userInfo().fullName()).isEqualTo(createRqDto.fullName());
        assertThat(infoRsDto.userInfo().email()).isEqualTo(userEmail);
        assertThat(infoRsDto.createdAt()).isBetween(now().minus(10, MINUTES), now());
        assertThat(infoRsDto.status()).isEqualTo(CreditStatusEnum.PENDING);
        assertThat(infoRsDto.loanDetails().requestedAmount()).isEqualTo(createRqDto.requestedAmount());
        assertThat(infoRsDto.loanDetails().termMonths()).isEqualTo(createRqDto.termMonths());
        assertThat(infoRsDto.loanDetails().interestRate()).isNull();
    }

    @Test
    void getInfoByCreditManager() {
        // given
        var userId = UUID.randomUUID();
        var userEmail = "userEmail@mail.com";
        var creditOwnerToken = jwtGenerator.generate(userId, userEmail);
        var createRqDto = buildCreditCreateRqDto();
        var createdRsDto = performPost("/credit-service/api/v1/create", createRqDto, CreditCreateRsDto.class, status().isOk(), creditOwnerToken);
        var creditManagerToken = jwtGenerator.generate(Set.of(ROLE_CREDIT_MANAGER));

        // when
        var infoRsDto = performGet("/credit-service/api/v1/info/" + createdRsDto.id(), CreditInfoRsDto.class, status().isOk(), creditManagerToken);

        // then
        assertThat(infoRsDto.id()).isEqualTo(createdRsDto.id());
        assertThat(infoRsDto.userInfo().userId()).isEqualTo(userId);
        assertThat(infoRsDto.userInfo().fullName()).isEqualTo(createRqDto.fullName());
        assertThat(infoRsDto.userInfo().email()).isEqualTo(userEmail);
        assertThat(infoRsDto.createdAt()).isBetween(now().minus(10, MINUTES), now());
        assertThat(infoRsDto.status()).isEqualTo(CreditStatusEnum.PENDING);
        assertThat(infoRsDto.loanDetails().requestedAmount()).isEqualTo(createRqDto.requestedAmount());
        assertThat(infoRsDto.loanDetails().termMonths()).isEqualTo(createRqDto.termMonths());
        assertThat(infoRsDto.loanDetails().interestRate()).isNull();
    }

    @Test
    void getInfoForbiddenForNoManager() {
        // given
        var creditOwnerToken = jwtGenerator.generate();
        var createRqDto = buildCreditCreateRqDto();
        var createdRsDto = performPost("/credit-service/api/v1/create", createRqDto, CreditCreateRsDto.class, status().isOk(), creditOwnerToken);
        var noManagerToken = jwtGenerator.generate();

        // when
        var rsDto = performGet("/credit-service/api/v1/info/" + createdRsDto.id(), ErrorRsDto.class, status().isForbidden(), noManagerToken);

        // then
        assertEquals("You are not allowed to view this credit", rsDto.message());
    }
}