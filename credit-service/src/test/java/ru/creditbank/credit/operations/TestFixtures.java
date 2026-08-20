package ru.creditbank.credit.operations;

import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;

import java.math.BigDecimal;

public class TestFixtures {

    public static CreditCreateRqDto buildCreditCreateRqDto() {

        return CreditCreateRqDto.builder()
                .fullName("Иванов Иван Иванович")
                .requestAmount(BigDecimal.valueOf(1234, 56))
                .termMonths(12)
                .build();
    }
}
