package ru.creditbank.credit.operations.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.creditbank.common.library.dto.loan.management.rs.UserLoanPaymentsStatisticRsDto;
import ru.creditbank.credit.operations.dto.ScoringInputDto;
import ru.creditbank.credit.operations.enitity.CreditEntity;

@Mapper(componentModel = "spring")
public interface CreditScoreMapper {

    @Mapping(target = "userId", source = "creditEntity.userId")
    @Mapping(target = "creditId", source = "creditEntity.id")
    @Mapping(target = "employmentMonths", source = "creditEntity.employmentMonths")
    @Mapping(target = "termMonths", source = "creditEntity.termMonths")
    @Mapping(target = "requestedAmount", source = "creditEntity.requestedAmount")
    @Mapping(target = "monthlyIncome", source = "creditEntity.monthlyIncome")
    @Mapping(target = "allDonePayments", source = "statistic.allDonePayments")
    @Mapping(target = "allOverduePayments", source = "statistic.allOverduePayments")
    ScoringInputDto toScoringInputDto(CreditEntity creditEntity, UserLoanPaymentsStatisticRsDto statistic);
}
