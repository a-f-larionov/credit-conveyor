package ru.creditbank.credit.operations.event;

import ru.creditbank.common.library.dto.loan.management.rs.UserLoanPaymentsStatisticRsDto;

import java.util.UUID;

public record ScoringPreparedEvent(UUID creditId, UserLoanPaymentsStatisticRsDto statisticRsDto) {

}
