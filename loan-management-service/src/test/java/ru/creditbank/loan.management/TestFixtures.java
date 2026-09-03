package ru.creditbank.loan.management;

import ru.creditbank.loan.management.dto.rq.CreateLoanRqDto;
import ru.creditbank.loan.management.dto.rq.PaymentRqDto;
import ru.creditbank.loan.management.enums.PaymentTypeEnum;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TestFixtures {

    private static final AtomicLong counter = new AtomicLong(1);

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

    public static PaymentRqDto buildPaymentsRqDto(UUID loanId, BigDecimal amount, PaymentTypeEnum type) {
        return PaymentRqDto.builder()
                .loanId(loanId)
                .amount(amount)
                .type(type)
                .build();
    }

    public static PaymentRqDto buildPaymentsRqDto(UUID loanId) {
        return PaymentRqDto.builder()
                .loanId(loanId)
                .amount(new BigDecimal(counter.getAndIncrement() + 5_000))
                .type(PaymentTypeEnum.REGULAR)
                .build();
    }
}
