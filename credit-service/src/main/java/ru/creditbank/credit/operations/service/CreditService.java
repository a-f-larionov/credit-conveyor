package ru.creditbank.credit.operations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.creditbank.common.library.enums.UserRole;
import ru.creditbank.common.library.jwt.JwtUserDetails;
import ru.creditbank.common.library.service.SecurityService;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;
import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rq.StatusUpdateRqDto;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;
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
import static ru.creditbank.credit.operations.dto.CreditStatusEnum.*;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final NotificationService notificationService;
    private final SecurityService securityService;

    private static final Set<CreditStatusEnum> allowedStatusesToChange = EnumSet.of(APPROVED, REJECTED);

    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    @Transactional
    public CreditCreateRsDto create(CreditCreateRqDto rqDto) {

        var userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var credit = creditMapper.mapRqDtoToCreateEntity(
                rqDto,
                userDetails.getId(),
                userDetails.getUsername(),
                PENDING,
                Instant.now(),
                Instant.now()
        );

        creditRepository.save(credit);

        return creditMapper.mapEntityToCreateRsDto(credit);
    }

    @Transactional(readOnly = true)
    public CreditInfoRsDto getInfo(UUID creditId) {

        var creditEntity = creditRepository.findById(creditId)
                .orElseThrow(() -> new CreditNotFoundException(creditId));

        securityService.checkAccess(creditEntity.getUserEmail(), UserRole.ROLE_ADMIN, UserRole.ROLE_CREDIT_MANAGER);

        return creditMapper.mapEntityToInfoRsDto(creditEntity);
    }

    @Transactional
    public void statusUpdate(StatusUpdateRqDto statusUpdateRqDto, UUID creditId) {

        var creditEntity = creditRepository.findById(creditId)
                .orElseThrow(() -> new CreditNotFoundException(creditId));

        validateCreditStatusMayChanged(creditEntity.getStatus());
        validateStatusIsAllowedToChange(statusUpdateRqDto.status());

        creditEntity.setStatus(statusUpdateRqDto.status());
        creditEntity.setManagerComment(statusUpdateRqDto.managerComment());

        notificationService.onCreditStatusChange(creditEntity);

        creditRepository.save(creditEntity);
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

