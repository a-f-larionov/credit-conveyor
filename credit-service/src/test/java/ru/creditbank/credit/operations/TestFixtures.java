package ru.creditbank.credit.operations;

import ru.creditbank.credit.operations.dto.CreditStatusEnum;
import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rq.StatusUpdateRqDto;

import java.math.BigDecimal;

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

    public static StatusUpdateRqDto buildStatusUpdateRqDto(CreditStatusEnum status, String managerComment) {
        return StatusUpdateRqDto.builder()
                .status(status)
                .managerComment(managerComment)
                .build();
    }
}
