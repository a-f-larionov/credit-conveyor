package ru.creditbank.credit.operations.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rq.StatusUpdateRqDto;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;
import ru.creditbank.credit.operations.service.CreditService;

import java.util.UUID;

@RestController
@RequestMapping("/credit-service/api/v1/")
@RequiredArgsConstructor
@Slf4j
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/create")
    public CreditCreateRsDto create(@Valid @RequestBody CreditCreateRqDto rqDto) {
        log.info("Create credit request: {}, {}", rqDto.fullName(), rqDto.requestedAmount());
        return creditService.create(rqDto);
    }

    @GetMapping("/info/{creditId}")
    public CreditInfoRsDto info(@PathVariable UUID creditId) {
        log.info("Fetching info: {}", creditId);
        return creditService.getInfo(creditId);
    }

    @PatchMapping("/status/update/{creditId}")
    public void statusUpdate(@Valid @RequestBody StatusUpdateRqDto statusUpdateRqDto, @PathVariable UUID creditId) {
        log.info("Status Update for creditId: {}", creditId);
        creditService.statusUpdate(statusUpdateRqDto, creditId);
    }
}
