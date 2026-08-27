package ru.creditbank.credit.operations.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;
import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;
import ru.creditbank.credit.operations.enitity.CreditEntity;

import java.time.Instant;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CreditMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "userEmail", source = "userEmail")
    @Mapping(target = "userFullName", source = "rqDto.fullName")
    @Mapping(target = "requestedAmount", source = "rqDto.requestedAmount")
    @Mapping(target = "termMonths", source = "rqDto.termMonths")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "creationDate", source = "creationDate")
    @Mapping(target = "lastUpdated", source = "lastUpdated")
    @Mapping(target = "interestRate", ignore = true)
    CreditEntity mapRqDtoToCreateEntity(CreditCreateRqDto rqDto, UUID userId, String userEmail, CreditStatusEnum status, Instant creationDate, Instant lastUpdated);

    @Mapping(target = "id", source = "credit.id")
    @Mapping(target = "status", source = "credit.status")
    @Mapping(target = "createdAt", source = "credit.creationDate")
    CreditCreateRsDto mapEntityToCreateRsDto(CreditEntity credit);

    @Mapping(target = "id", source = "credit.id")
    @Mapping(target = "userInfo.userId", source = "credit.userId")
    @Mapping(target = "userInfo.fullName", source = "credit.userFullName")
    @Mapping(target = "userInfo.email", source = "credit.userEmail")
    @Mapping(target = "loanDetails.requestedAmount", source = "credit.requestedAmount")
    @Mapping(target = "loanDetails.termMonths", source = "credit.termMonths")
    @Mapping(target = "status", source = "credit.status")
    @Mapping(target = "createdAt", source = "credit.creationDate")
    CreditInfoRsDto mapEntityToInfoRsDto(CreditEntity credit);
}
