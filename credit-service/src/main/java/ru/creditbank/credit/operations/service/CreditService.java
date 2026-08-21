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

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;

    @Transactional
    public CreditCreateRsDto create(CreditCreateRqDto rqDto) {

        var userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var credit = mapRqDtoToEntity(rqDto, userDetails);

        creditRepository.save(credit);

        return mapEntityToCreateRsDto(credit);
    }

    public CreditInfoRsDto info(CreditInfoRqDto rqDto) {

        var creditEntity = creditRepository.findById(rqDto.id())
                .orElseThrow(() -> new CreditException("Credit not found"));

        return mapEntityToInfoRsDto(creditEntity);
    }

    private static CreditInfoRsDto mapEntityToInfoRsDto(CreditEntity creditEntity) {
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

    private static CreditCreateRsDto mapEntityToCreateRsDto(CreditEntity credit) {
        return CreditCreateRsDto.builder()
                .id(credit.getId())
                .createdAt(credit.getCreationDate())
                .status(credit.getStatus())
                .build();
    }

    private static CreditEntity mapRqDtoToEntity(CreditCreateRqDto rqDto, JwtUserDetails userDetails) {
        return CreditEntity.builder()
                .userId(userDetails.getUserId())
                .userFullName(rqDto.fullName())
                .requestedAmount(rqDto.requestAmount())
                .termMonths(rqDto.termMonths())
                .status(CreditStatusEnum.PENDING)
                .lastUpdated(Instant.now())
                .creationDate(Instant.now())
                .build();
    }
}
