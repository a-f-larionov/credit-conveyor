package ru.creditbank.credit.operations.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.creditbank.credit.operations.dto.CreditStatusEnum;
import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rq.StatusUpdateRqDto;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;
import ru.creditbank.credit.operations.enitity.CreditEntity;
import ru.creditbank.credit.operations.exception.AccessNotAllowed;
import ru.creditbank.credit.operations.exception.CreditNotFoundException;
import ru.creditbank.credit.operations.exception.CreditStatusUpdate;
import ru.creditbank.credit.operations.jwt.JwtUserDetails;
import ru.creditbank.credit.operations.mappers.CreditMapper;
import ru.creditbank.credit.operations.repository.CreditRepository;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.*;
import static ru.creditbank.credit.operations.dto.CreditStatusEnum.PENDING;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final NotificationService notificationService;

    private static final Set<CreditStatusEnum> allowedStatuses =
            EnumSet.of(CreditStatusEnum.APPROVED, CreditStatusEnum.REJECTED);

    private final CreditRepository creditRepository;
    private final CreditMapper creditMapper;

    @Transactional
    public CreditCreateRsDto create(CreditCreateRqDto rqDto) {

        var userDetails = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var credit = creditMapper.mapRqDtoToCreateEntity(
                rqDto,
                userDetails.getUserId(),
                userDetails.getUsername(),
                PENDING,
                Instant.now(),
                Instant.now()
        );

        creditRepository.save(credit);

        return creditMapper.mapEntityToCreateRsDto(credit);
    }

    public CreditInfoRsDto getInfo(UUID creditId) {

        var creditEntity = creditRepository.findById(creditId)
                .orElseThrow(() -> new CreditNotFoundException(
                        format("Credit with id %s not found", creditId), NOT_FOUND)
                );

        validateAccess(creditEntity);

        return creditMapper.mapEntityToInfoRsDto(creditEntity);
    }

    @Transactional
    public void statusUpdate(StatusUpdateRqDto statusUpdateRqDto, UUID creditId) {

        var creditEntity = creditRepository.findById(creditId)
                .orElseThrow(() -> new CreditNotFoundException(
                        format("Credit with id %s not found", creditId),
                        NOT_FOUND));

        validateCreditStatusMayChanged(creditEntity.getStatus());
        validateStatusIsAllowedToChange(statusUpdateRqDto.status());

        creditEntity.setStatus(statusUpdateRqDto.status());
        creditEntity.setManagerComment(statusUpdateRqDto.managerComment());

        notificationService.onCreditStatusChange(creditEntity);

        creditRepository.save(creditEntity);
    }

    private void validateCreditStatusMayChanged(CreditStatusEnum status) {
        if (!status.equals(PENDING)) {
            throw new CreditStatusUpdate(
                    format("Credit must be %s, but is %s", PENDING, status),
                    BAD_REQUEST);
        }
    }

    private void validateStatusIsAllowedToChange(CreditStatusEnum status) {
        if (!allowedStatuses.contains(status)) {
            throw new CreditStatusUpdate(
                    format("Target status '%s' is not allowed. Allowed values: %s",
                            status, allowedStatuses),
                    BAD_REQUEST);
        }
    }

    private void validateAccess(CreditEntity creditEntity) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (JwtUserDetails) auth.getPrincipal();

        boolean isAdminOrManager = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_CREDIT_MANAGER".equals(a.getAuthority()));

        if (!isAdminOrManager && (!creditEntity.getUserId().equals(userDetails.getUserId()))) {
            throw new AccessNotAllowed("You are not allowed to view this credit", FORBIDDEN);
        }
    }
}

