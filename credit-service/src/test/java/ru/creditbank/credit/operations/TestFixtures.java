package ru.creditbank.credit.operations;

import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rq.CreditInfoRqDto;

import java.math.BigDecimal;
import java.util.UUID;

public class TestFixtures {

    public static CreditCreateRqDto buildCreditCreateRqDto() {

        return CreditCreateRqDto.builder()
                .fullName("Иванов Иван Иванович")
                .requestAmount(BigDecimal.valueOf(1234, 2))
                .termMonths(12)
                .build();
    }

    public static CreditInfoRqDto buildCreditInfoRqDto(UUID id) {
        return CreditInfoRqDto.builder()
                .id(id)
                .build();
    }
}
