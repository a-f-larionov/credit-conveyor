package ru.creditbank.credit.operations;

import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rq.CreditInfoRqDto;

import java.math.BigDecimal;
import java.util.UUID;

public class TestFixtures {

    public static CreditCreateRqDto buildCreditCreateRqDto() {
        return buildCreditCreateRqDto("Иванов Иван Иванович");
    }

    public static CreditCreateRqDto buildCreditCreateRqDto(String fullName) {

        return CreditCreateRqDto.builder()
                .fullName(fullName)
                .requestedAmount(new BigDecimal("123.23"))
                .termMonths(12)
                .build();
    }

    public static CreditInfoRqDto buildCreditInfoRqDto(UUID id) {
        return CreditInfoRqDto.builder()
                .id(id)
                .build();
    }
}
