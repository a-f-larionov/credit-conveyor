package ru.creditbank.loan.management.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import ru.creditbank.common.library.dto.rs.ErrorRsDto;
import ru.creditbank.loan.management.SpringBootMvcBaseTest;
import ru.creditbank.loan.management.TestJwtGenerator;
import ru.creditbank.loan.management.dto.rq.CreateLoanRqDto;
import ru.creditbank.loan.management.dto.rs.LoanListRsDto;
import ru.creditbank.loan.management.dto.rs.LoanRsDto;
import ru.creditbank.loan.management.enums.LoanStatusEnum;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.creditbank.common.library.enums.UserRole.ROLE_CREDIT_MANAGER;
import static ru.creditbank.common.library.enums.UserRole.ROLE_USER;
import static ru.creditbank.loan.management.TestFixtures.buildCreateLoanRqDto;

class LoanControllerTest extends SpringBootMvcBaseTest {

    @Autowired
    TestJwtGenerator jwtGenerator;

    @Test
    void createAndInfo() {
        // given
        var userId = UUID.randomUUID();
        var managerToken = jwtGenerator.generate(Set.of(ROLE_CREDIT_MANAGER));
        var rqDto = buildCreateLoanRqDto(userId);

        // when
        var createdRsDto = performPost("/loan-management-service/api/v1/loans/create", rqDto, LoanRsDto.class, status().isCreated(), managerToken);
        var getInfoRsDto = performGet("/loan-management-service/api/v1/loans/info/" + createdRsDto.loanId(), LoanRsDto.class, managerToken);

        // then
        assertRsDtoIsEqualsToRqDto(createdRsDto, rqDto, createdRsDto.loanId(), userId);
        assertRsDtoIsEqualsToRqDto(getInfoRsDto, rqDto, createdRsDto.loanId(), userId);
    }

    @Test
    void list() {
        // given
        var managerToken = jwtGenerator.generate(Set.of(ROLE_CREDIT_MANAGER));
        var userId = UUID.randomUUID();
        var rqDto1 = buildCreateLoanRqDto(userId, new BigDecimal(5_000_001));
        var rqDto2 = buildCreateLoanRqDto(userId, new BigDecimal(5_000_001));
        var createdRsDto1 = performPost("/loan-management-service/api/v1/loans/create", rqDto1, LoanRsDto.class, status().isCreated(), managerToken);
        var createdRsDto2 = performPost("/loan-management-service/api/v1/loans/create", rqDto2, LoanRsDto.class, status().isCreated(), managerToken);

        // when
        var rsDto = performGet("/loan-management-service/api/v1/loans/list/" + userId, LoanListRsDto.class, managerToken);

        // then
        assertThat(rsDto.loans()).hasSize(2);

        var rsDto1 = findByLoanId(rsDto.loans(), createdRsDto1.loanId());
        var rsDto2 = findByLoanId(rsDto.loans(), createdRsDto2.loanId());

        assertRsDtoIsEqualsToRqDto(rsDto1, rqDto1, createdRsDto1.loanId(), userId);
        assertRsDtoIsEqualsToRqDto(rsDto2, rqDto2, createdRsDto2.loanId(), userId);
    }

    @Test
    void create_userRole_shouldReturn403() {
        // given
        var userId = UUID.randomUUID();
        var userToken = jwtGenerator.generate(Set.of(ROLE_USER));
        var rqDto = buildCreateLoanRqDto(userId);

        // when
        var rsDto = performPost("/loan-management-service/api/v1/loans/create", rqDto,
                ErrorRsDto.class, status().isForbidden(), userToken);

        // then
        assertThat(rsDto.error()).isEqualTo("Forbidden");
    }

    @Test
    void info_userHasOwnAccess() {
        // given
        var ownerId = UUID.randomUUID();
        var managerToken = jwtGenerator.generate(Set.of(ROLE_CREDIT_MANAGER));
        var ownerToken = jwtGenerator.generate(ownerId, Set.of(ROLE_USER));
        var rqDto = buildCreateLoanRqDto(ownerId);
        var created = performPost("/loan-management-service/api/v1/loans/create", rqDto, LoanRsDto.class, status().isCreated(), managerToken);

        //when
        var rsDto = performGet("/loan-management-service/api/v1/loans/info/" + created.loanId(), LoanRsDto.class,
                status().isOk(), ownerToken);

        // then
        assertRsDtoIsEqualsToRqDto(rsDto, rqDto, rsDto.loanId(), ownerId);
    }

    @Test
    void info_otherUser__shouldReturn403() {
        // given
        var ownerId = UUID.randomUUID();
        var managerToken = jwtGenerator.generate(Set.of(ROLE_CREDIT_MANAGER));
        var rqDto = buildCreateLoanRqDto(ownerId);
        var created = performPost("/loan-management-service/api/v1/loans/create", rqDto, LoanRsDto.class, status().isCreated(), managerToken);

        var otherUserId = UUID.randomUUID();
        var otherUserToken = jwtGenerator.generate(otherUserId, Set.of(ROLE_USER));

        //when
        var rsDto = performGet("/loan-management-service/api/v1/loans/info/" + created.loanId(), ErrorRsDto.class,
                status().isForbidden(), otherUserToken);

        // then
        assertThat(rsDto.error()).isEqualTo("Forbidden");
    }

    @Test
    void list_otherUser_shouldReturn403() {
        // given
        var ownerId = UUID.randomUUID();
        var managerToken = jwtGenerator.generate(Set.of(ROLE_CREDIT_MANAGER));

        performPost("/loan-management-service/api/v1/loans/create", buildCreateLoanRqDto(ownerId), LoanRsDto.class, status().isCreated(), managerToken);
        performPost("/loan-management-service/api/v1/loans/create", buildCreateLoanRqDto(ownerId), LoanRsDto.class, status().isCreated(), managerToken);

        // when
        var otherUserId = UUID.randomUUID();
        var otherUserToken = jwtGenerator.generate(Set.of(ROLE_USER));

        var rsDto = performGet("/loan-management-service/api/v1/loans/list/" + otherUserId, ErrorRsDto.class,
                status().isForbidden(), otherUserToken);

        // then
        assertThat(rsDto.error()).isEqualTo("Forbidden");
    }


    private void assertRsDtoIsEqualsToRqDto(LoanRsDto rsDto, CreateLoanRqDto rqDto, UUID loanId, UUID userId) {
        assertThat(rsDto.loanId()).isEqualTo(loanId);
        assertThat(rsDto.userId()).isEqualTo(userId);
        assertThat(rsDto.totalAmount()).isEqualByComparingTo(rqDto.totalAmount());
        assertThat(rsDto.remainingAmount()).isEqualByComparingTo(rqDto.totalAmount());
        assertThat(rsDto.interestRate()).isEqualByComparingTo(rqDto.interestRate());
        assertThat(rsDto.nextPaymentDate()).isEqualTo(rqDto.firstPaymentDate().truncatedTo(ChronoUnit.DAYS));
        assertThat(rsDto.termMonths()).isEqualTo(rqDto.termMonths());
        assertThat(rsDto.status()).isEqualTo(LoanStatusEnum.ACTIVE);
    }

    @Nullable
    private LoanRsDto findByLoanId(List<LoanRsDto> list, UUID loanId) {
        return list.stream()
                .filter(item -> item.loanId().compareTo(loanId) == 0)
                .findAny()
                .orElse(null);
    }
}