package ru.creditbank.loan.management.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.creditbank.loan.management.dto.rq.CreateLoanRqDto;
import ru.creditbank.loan.management.dto.rs.LoanListRsDto;
import ru.creditbank.loan.management.dto.rs.LoanPaymentScheduleRsDto;
import ru.creditbank.loan.management.dto.rs.LoanPaymentsScheduleListRsDto;
import ru.creditbank.loan.management.dto.rs.LoanRsDto;
import ru.creditbank.loan.management.enitity.LoanEntity;
import ru.creditbank.loan.management.enitity.SchedulePaymentEntity;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "rqDto.userId")
    @Mapping(target = "totalAmount", source = "rqDto.totalAmount")
    @Mapping(target = "remainingAmount", source = "rqDto.totalAmount")
    @Mapping(target = "termMonths", source = "rqDto.termMonths")
    @Mapping(target = "interestRate", source = "rqDto.interestRate")
    @Mapping(target = "nextPaymentDate", expression = "java(rqDto.firstPaymentDate().truncatedTo(java.time.temporal.ChronoUnit.DAYS))")
    @Mapping(target = "status", expression = "java(ru.creditbank.loan.management.enums.LoanStatusEnum.ACTIVE)")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "payments", ignore = true)
    @Mapping(target = "schedulePayments", ignore = true)
    LoanEntity toEntityForCreate(CreateLoanRqDto rqDto);

    @Mapping(target = "loanId", source = "entity.id")
    @Mapping(target = "userId", source = "entity.userId")
    @Mapping(target = "totalAmount", source = "entity.totalAmount")
    @Mapping(target = "remainingAmount", source = "entity.remainingAmount")
    @Mapping(target = "nextPaymentDate", source = "entity.nextPaymentDate")
    @Mapping(target = "termMonths", source = "entity.termMonths")
    @Mapping(target = "interestRate", source = "entity.interestRate")
    @Mapping(target = "status", source = "entity.status")
    LoanRsDto entityToRsDto(LoanEntity entity);

    List<LoanRsDto> mapToLoanList(List<LoanEntity> list);

    default LoanListRsDto toLoanListRsDto(List<LoanRsDto> list) {
        return LoanListRsDto.builder().loans(list).build();
    }

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
