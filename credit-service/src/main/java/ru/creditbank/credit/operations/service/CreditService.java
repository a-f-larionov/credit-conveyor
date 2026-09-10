package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;
import ru.creditbank.common.library.dto.credit.rs.StatusUpdateRsDto;
import ru.creditbank.common.library.enums.CreditStatusEnum;
import ru.creditbank.common.library.enums.UserRole;
import ru.creditbank.common.library.service.SecurityService;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;
import ru.creditbank.credit.operations.event.CreditCreatedEvent;
import ru.creditbank.credit.operations.exception.CreditNotFoundException;
import ru.creditbank.credit.operations.exception.CreditStatusUpdateException;
import ru.creditbank.credit.operations.mappers.CreditMapper;
import ru.creditbank.credit.operations.repository.CreditRepository;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static ru.creditbank.common.library.enums.CreditStatusEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {

    private static final Set<CreditStatusEnum> allowedStatusesToChange = EnumSet.of(APPROVED, REJECTED);
    private final ApplicationEventPublisher applicationEventPublisher;

    private final NotificationService notificationService;
    private final SecurityService securityService;
    private final InterestRateService interestRateService;
    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    @Transactional
    public CreditCreateRsDto create(CreditCreateRqDto rqDto) {
        log.info("Create credit request: {}", rqDto.toString());

        var userDetails = securityService.getUserDetails();

        var credit = creditMapper.mapRqDtoToCreateEntity(
                rqDto,
                userDetails.getId(),
                userDetails.getUsername(),
                PENDING,
                Instant.now(),
                Instant.now()
        );

        creditRepository.save(credit);

        applicationEventPublisher.publishEvent(new CreditCreatedEvent(credit.getId(), credit.getUserId()));

        return creditMapper.mapEntityToCreateRsDto(credit);
    }

    @Transactional(readOnly = true)
    public CreditInfoRsDto getInfo(UUID creditId) {
        log.info("Fetching info: {}", creditId);
        var creditEntity = creditRepository.findById(creditId)
                .orElseThrow(() -> new CreditNotFoundException(creditId));

        securityService.checkAccess(creditEntity.getUserEmail(), UserRole.ROLE_ADMIN, UserRole.ROLE_CREDIT_MANAGER);
        return creditMapper.mapEntityToInfoRsDto(creditEntity);
    }

    @Transactional
    public StatusUpdateRsDto statusUpdate(UUID creditId, StatusUpdateRqDto statusUpdateRqDto) {
        log.info("Status update for creditId: creditId={} rqDto={}", creditId, statusUpdateRqDto);
        var creditEntity = creditRepository.findById(creditId)
                .orElseThrow(() -> new CreditNotFoundException(creditId));
        validateStatusIsAllowedToChange(statusUpdateRqDto.status());
        validateCreditStatusMayChanged(creditEntity.getStatus());

        if (creditEntity.getScore() != null && statusUpdateRqDto.status() == APPROVED) {
            creditEntity.setInterestRate(interestRateService.calcInterestRate(creditEntity));
        }
        creditEntity.setStatus(statusUpdateRqDto.status());
        creditEntity.setManagerComment(statusUpdateRqDto.managerComment());
        creditRepository.save(creditEntity);

        notificationService.onCreditStatusChange(creditEntity);

        return creditMapper.mapEntityToStatusRsDto(creditEntity);
    }

    private void validateCreditStatusMayChanged(CreditStatusEnum status) {
        if (!status.equals(PENDING)) {
            throw new CreditStatusUpdateException(
                    format("Credit must be %s, but is %s", PENDING, status),
                    BAD_REQUEST);
        }
    }

    private void validateStatusIsAllowedToChange(CreditStatusEnum status) {
        if (!allowedStatusesToChange.contains(status)) {
            throw new CreditStatusUpdateException(
                    format("Target status '%s' is not allowed. Allowed values: %s",
                            status, allowedStatusesToChange),
                    BAD_REQUEST);
        }
    }
}

