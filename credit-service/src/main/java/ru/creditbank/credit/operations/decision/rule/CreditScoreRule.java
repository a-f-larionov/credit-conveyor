package ru.creditbank.credit.operations.decision.rule;

import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.loan.management.rs.ClientLoanPaymentsStatisticRsDto;

import java.util.UUID;

public interface CreditScoreRule {

    Long evaluate(UUID creditId, CreditCreateRqDto creditEntity, ClientLoanPaymentsStatisticRsDto history);

    String getDescription();
}