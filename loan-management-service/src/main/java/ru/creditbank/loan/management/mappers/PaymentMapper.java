package ru.creditbank.loan.management.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.creditbank.loan.management.dto.rq.PaymentRqDto;
import ru.creditbank.loan.management.dto.rs.PaymentHistoryItemRsDto;
import ru.creditbank.loan.management.dto.rs.PaymentHistoryRsDto;
import ru.creditbank.loan.management.dto.rs.PaymentRsDto;
import ru.creditbank.loan.management.enitity.PaymentEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "loan.id", source = "rqDto.loanId")
    @Mapping(target = "amount", source = "rqDto.amount")
    @Mapping(target = "remaining", source = "remaining")
    @Mapping(target = "type", source = "rqDto.type")
    @Mapping(target = "createdAt", source = "createdAt")
    PaymentEntity toEntityForCreate(PaymentRqDto rqDto, BigDecimal remaining, Instant createdAt);

    @Mapping(target = "paymentId", source = "entity.id")
    @Mapping(target = "nextPaymentDate", source = "nextPaymentDate")
    @Mapping(target = "principalRemainingAmount", source = "principalRemainingAmount")
    PaymentRsDto toRsDto(PaymentEntity entity, Instant nextPaymentDate, BigDecimal principalRemainingAmount);

    @Mapping(target = "paymentId", source = "entity.id")
    @Mapping(target = "amount", source = "entity.amount")
    @Mapping(target = "datetime", source = "entity.createdAt")
    @Mapping(target = "type", source = "entity.type")
    @Mapping(target = "newBalance", source = "entity.remaining")
    PaymentHistoryItemRsDto toRsDto(PaymentEntity entity);

    List<PaymentHistoryItemRsDto> mapToList(List<PaymentEntity> entity);

    default PaymentHistoryRsDto toPaymentHistoryRsDto(List<PaymentHistoryItemRsDto> list) {
        return PaymentHistoryRsDto.builder()
                .payments(list)
                .build();
    }
}
