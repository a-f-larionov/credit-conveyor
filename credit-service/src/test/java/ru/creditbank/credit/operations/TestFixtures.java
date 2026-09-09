package ru.creditbank.credit.operations;

import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.ClientLoanPaymentsStatisticRsDto;
import ru.creditbank.common.library.enums.CreditStatusEnum;

import java.math.BigDecimal;

public class TestFixtures {

    public static CreditCreateRqDto buildCreditCreateRqDto() {
        return buildCreditCreateRqDto("Иванов Иван Иванович");
    }

    public static CreditCreateRqDto buildCreditCreateRqDto(String fullName) {
        return buildCreditCreateRqDto(fullName, 12, new BigDecimal("100000.0"));
    }

    public static CreditCreateRqDto buildCreditCreateRqDto(String fullName, int employmentMonths, BigDecimal monthlyIncome) {
        return buildCreditCreateRqDto(fullName, employmentMonths, monthlyIncome, new BigDecimal("100000"));
    }

    public static CreditCreateRqDto buildCreditCreateRqDto(String fullName, int employmentMonths, BigDecimal monthlyIncome, BigDecimal requestedAmount) {
        return CreditCreateRqDto.builder()
                .fullName(fullName)
                .requestedAmount(requestedAmount)
                .termMonths(12)
                .employmentMonths(employmentMonths)
                .monthlyIncome(monthlyIncome)
                .build();
    }

    public static StatusUpdateRqDto buildStatusUpdateRqDto(CreditStatusEnum status, String managerComment) {
        return StatusUpdateRqDto.builder()
                .status(status)
                .managerComment(managerComment)
                .build();
    }

    public static ClientLoanPaymentsStatisticRsDto buildClientLoanPaymentsStatisticRsDto(
            Integer allDonePayments,
            Integer allOverduePayments) {
        return ClientLoanPaymentsStatisticRsDto
                .builder()
                .allDonePayments(allDonePayments)
                .allOverduePayments(allOverduePayments)
                .build();
    }
}
