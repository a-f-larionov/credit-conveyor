package ru.creditbank.credit.operations.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;
import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreditCreateService {

    public CreditCreateRsDto create(CreditCreateRqDto rqDto) {
        return CreditCreateRsDto.builder()
                .id(UUID.randomUUID())
                .createAt(new Date(System.currentTimeMillis()))
                .status(CreditStatusEnum.APPROVED)
                .build();
    }
}
