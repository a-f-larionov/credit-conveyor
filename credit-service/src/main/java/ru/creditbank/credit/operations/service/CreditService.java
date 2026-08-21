package ru.creditbank.credit.operations.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;
import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rq.CreditInfoRqDto;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;
import ru.creditbank.credit.operations.enitity.CreditEntity;
import ru.creditbank.credit.operations.exception.CreditException;
import ru.creditbank.credit.operations.jwt.JwtUserDetails;
import ru.creditbank.credit.operations.repository.CreditRepository;

import java.time.Instant;

import static jakarta.transaction.Transactional.TxType.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
@Transactional(REQUIRES_NEW)
public class CreditService {

    private final CreditRepository creditRepository;

    public CreditCreateRsDto create(CreditCreateRqDto rqDto) {

        var userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var credit = CreditEntity.builder()
                .userId(userDetails.getUserId())
                .userFullName(rqDto.fullName())
                .requestedAmount(rqDto.requestAmount())
                .termMonths(rqDto.termMonths())
                .status(CreditStatusEnum.PENDING)
                .lastUpdated(Instant.now())
                .creationDate(Instant.now())
                .build();

        creditRepository.save(credit);

        return CreditCreateRsDto.builder()
                .id(credit.getId())
                .createdAt(credit.getCreationDate())
                .status(credit.getStatus())
                .build();
    }

    public CreditInfoRsDto info(CreditInfoRqDto rqDto) {

        var creditEntity = creditRepository.findById(rqDto.id())
                .orElseThrow(() -> new CreditException("Credit not found"));

        return CreditInfoRsDto.builder()
                .id(creditEntity.getId())
                .userId(creditEntity.getUserId())
                .userFullName(creditEntity.getUserFullName())
                .requestedAmount(creditEntity.getRequestedAmount())
                .termMonths(creditEntity.getTermMonths())
                .status(creditEntity.getStatus())
                .creationDate(creditEntity.getCreationDate())
                .lastUpdated(creditEntity.getLastUpdated())
                .build();
    }
}
