package ru.creditbank.apigateway;

import org.springframework.security.web.webauthn.api.CredentialRecord;
import ru.creditbank.apigateway.dto.FullNameDto;
import ru.creditbank.apigateway.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;
import ru.creditbank.common.library.enums.CreditStatusEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TestFixtures {
    private static Long lastUniqueIndex = 0L;

    public static RegisterRqDto buildRegisterRqDto() {
        lastUniqueIndex++;
        return buildRegisterRqDto(
                "user" + lastUniqueIndex + "@email.ru",
                "Password" + lastUniqueIndex
        );
    }

    public static RegisterRqDto buildRegisterRqDto(String password) {
        lastUniqueIndex++;
        return buildRegisterRqDto(
                "user" + lastUniqueIndex + "@email.ru",
                password);
    }

    public static RegisterRqDto buildRegisterRqDto(String userEmail, String password) {
        return RegisterRqDto.builder()
                .fullName(FullNameDto.builder()
                        .firstName("firstName")
                        .middleName("middleName")
                        .lastName("lastName")
                        .build())
                .email(userEmail)
                .password(password)
                .build();
    }

    public static LoginRqDto buildLoginRqDto(RegisterRqDto registerRqDto) {
        return LoginRqDto.builder()
                .email(registerRqDto.email())
                .password(registerRqDto.password())
                .build();
    }

    public static UserInfoRqDto buildUserInfoRqDto(String email) {
        return UserInfoRqDto.builder()
                .email(email)
                .build();
    }

    public static LoginRqDto buildLoginRqDto(String email, String password) {
        return LoginRqDto.builder()
                .email(email)
                .password(password)
                .build();
    }

    public static UserInfoRsDto buildInfoRsDto() {
        return UserInfoRsDto.builder()
                .email("email@mail.ru")
                .build();
    }

    public static CreditCreateRqDto buildCreateRqDto() {
        return CreditCreateRqDto.builder()
                .fullName("full-name")
                .requestedAmount(new BigDecimal("10000000"))
                .termMonths(12)
                .build();
    }

    public static CreditCreateRsDto buildCreateRsDTo() {
        return CreditCreateRsDto.builder()
                .id(UUID.randomUUID())
                .status(CreditStatusEnum.PENDING)
                .createdAt(Instant.now())
                .build();
    }

    public static StatusUpdateRqDto buildUpdateStatusRqDto() {
        return StatusUpdateRqDto.builder()
                .managerComment("manager comment")
                .status(CreditStatusEnum.APPROVED)
                .build();
    }
}
