package ru.creditbank.loan.management;

import ru.creditbank.loan.management.dto.rq.CreateLoanRqDto;
import ru.creditbank.loan.management.dto.rs.PaymentRsDto;

public class TestFixtures {


    public static CreateLoanRqDto buildCreateLoanRqDto() {
        return CreateLoanRqDto.builder()
                .build();
    }

    public static PaymentRsDto buildPaymentsMakeRqDto() {
        return PaymentRsDto.builder()
                .build();
    }
}
