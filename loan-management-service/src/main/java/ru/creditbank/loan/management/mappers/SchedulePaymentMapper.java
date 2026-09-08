package ru.creditbank.loan.management.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.creditbank.loan.management.dto.rs.LoanPaymentScheduleRsDto;
import ru.creditbank.loan.management.dto.rs.LoanPaymentsScheduleListRsDto;
import ru.creditbank.loan.management.enitity.SchedulePaymentEntity;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SchedulePaymentMapper {

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "loanId", source = "entity.loan.id")
    @Mapping(target = "number", source = "entity.number")
    @Mapping(target = "date", source = "entity.date")
    @Mapping(target = "interestAmount", source = "entity.interestAmount")
    @Mapping(target = "principalAmount", source = "entity.principalAmount")
    @Mapping(target = "status", source = "entity.status")
    LoanPaymentScheduleRsDto entityToRsDto(SchedulePaymentEntity entity);

    List<LoanPaymentScheduleRsDto> mapToPaymentScheduleList(List<SchedulePaymentEntity> payments);

    default LoanPaymentsScheduleListRsDto toLoanPaymentsScheduleListRsDto(UUID loanId, List<LoanPaymentScheduleRsDto> schedulePayments) {
        return LoanPaymentsScheduleListRsDto.builder()
                .loanId(loanId)
                .payments(schedulePayments)
                .build();
    }

}
