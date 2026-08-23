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
import ru.creditbank.credit.operations.exception.CreditNotFoundException;
import ru.creditbank.credit.operations.jwt.JwtUserDetails;
import ru.creditbank.credit.operations.mappers.CreditMapper;
import ru.creditbank.credit.operations.repository.CreditRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    @Transactional
    public CreditCreateRsDto create(CreditCreateRqDto rqDto) {

        var userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var credit = creditMapper.mapRqDtoToCreateEntity(
                rqDto,
                userDetails.getUserId(),
                CreditStatusEnum.PENDING,
                Instant.now(),
                Instant.now()
        );

        creditRepository.save(credit);

        return creditMapper.mapEntityToCreateRsDto(credit);
    }

    public CreditInfoRsDto info(CreditInfoRqDto rqDto) {

        var creditEntity = creditRepository.findById(rqDto.id())
                .orElseThrow(() -> new CreditNotFoundException("Credit not found"));

        return creditMapper.mapEntityToInfoRsDto(creditEntity);
    }
}
