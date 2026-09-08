package ru.creditbank.apigateway;

import ru.creditbank.apigateway.dto.FullNameDto;
import ru.creditbank.apigateway.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.dto.rq.UserInfoRqDto;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;
import ru.creditbank.common.library.dto.loan.management.rq.CreateLoanRqDto;
import ru.creditbank.common.library.dto.loan.management.rq.PaymentRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.*;
import ru.creditbank.common.library.enums.CreditStatusEnum;
import ru.creditbank.common.library.enums.LoanStatusEnum;
import ru.creditbank.common.library.enums.PaymentTypeEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TestFixtures {

    private static Long lastUniqueIndex = 0L;
    private static final AtomicLong counter = new AtomicLong(1);

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

    public static CreditCreateRqDto buildCreditCreateRqDto() {
        return CreditCreateRqDto.builder()
                .fullName("full-name")
                .requestedAmount(new BigDecimal("10000000"))
                .termMonths(12)
                .monthlyIncome(new BigDecimal(10000))
                .employmentMonths(10)
                .build();
    }

    public static CreditCreateRsDto buildCreditCreateRsDTo() {
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

    public static CreateLoanRqDto buildCreateLoanRqDto() {
        return buildCreateLoanRqDto(UUID.randomUUID());
    }

    public static CreateLoanRqDto buildCreateLoanRqDto(UUID userId) {
        return buildCreateLoanRqDto(userId, new BigDecimal(counter.getAndIncrement() + 5_000_000));
    }

    public static CreateLoanRqDto buildCreateLoanRqDto(UUID userId, BigDecimal totalAmount) {
        return buildCreateLoanRqDto(userId, totalAmount, (int) counter.getAndIncrement(), new BigDecimal(10 + counter.getAndIncrement()));
    }

    public static CreateLoanRqDto buildCreateLoanRqDto(UUID userId, BigDecimal totalAmount, Integer termMonths, BigDecimal interestRate) {
        return CreateLoanRqDto.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .interestRate(interestRate)
                .termMonths(termMonths)
                .firstPaymentDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
    }

    public static PaymentRqDto buildPaymentRqDto(UUID loanId, BigDecimal amount, PaymentTypeEnum type) {
        return PaymentRqDto.builder()
                .loanId(loanId)
                .amount(amount)
                .type(type)
                .build();
    }

    public static PaymentRqDto buildPaymentRqDto(UUID loanId) {
        return PaymentRqDto.builder()
                .loanId(loanId)
                .amount(new BigDecimal(counter.getAndIncrement() + 5_000))
                .type(PaymentTypeEnum.REGULAR)
                .build();
    }

    public static LoanRsDto buildLoanRsDto() {
        return LoanRsDto.builder()
                .loanId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .totalAmount(new BigDecimal(123123))
                .interestRate(new BigDecimal(12))
                .termMonths(12)
                .status(LoanStatusEnum.ACTIVE)
                .remainingAmount(new BigDecimal(12312))
                .nextPaymentDate(Instant.now())
                .build();
    }

    public static LoanListRsDto buildLoanListRsDto() {
        return LoanListRsDto.builder()
                .loans(List.of(
                        buildLoanRsDto(),
                        buildLoanRsDto()
                ))
                .build();
    }

    public static PaymentRsDto buildPaymentRsDto() {
        return PaymentRsDto.builder()
                .paymentId(UUID.randomUUID())
                .nextPaymentDate(Instant.now())
                .principalRemainingAmount(new BigDecimal(12312))
                .build();
    }

    public static PaymentHistoryItemRsDto buildPaymentHistoryItemRsDto() {
        return PaymentHistoryItemRsDto.builder()
                .paymentId(UUID.randomUUID())
                .datetime(Instant.now())
                .amount(new BigDecimal(12312312))
                .newBalance(new BigDecimal(123232))
                .build();
    }

    public static PaymentHistoryRsDto buildPaymentHistoryRsDto() {
        return PaymentHistoryRsDto.builder()
                .payments(List.of(
                        buildPaymentHistoryItemRsDto(),
                        buildPaymentHistoryItemRsDto()
                ))
                .build();
    }
}
