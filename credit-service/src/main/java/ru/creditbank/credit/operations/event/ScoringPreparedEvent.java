package ru.creditbank.credit.operations.event;

import lombok.Getter;
import ru.creditbank.common.library.dto.loan.management.rs.ClientLoanPaymentsStatisticRsDto;

import java.util.UUID;

@Getter
public class ScoringPreparedEvent {

    private final UUID creditId;
    private final ClientLoanPaymentsStatisticRsDto statisticRsDto;

    public ScoringPreparedEvent(UUID creditId, ClientLoanPaymentsStatisticRsDto statisticRsDto) {
        this.creditId = creditId;
        this.statisticRsDto = statisticRsDto;
    }

}
