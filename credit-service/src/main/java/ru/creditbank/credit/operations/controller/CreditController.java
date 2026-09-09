package ru.creditbank.credit.operations.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.common.library.dto.credit.rq.CreditCreateRqDto;
import ru.creditbank.common.library.dto.credit.rq.StatusUpdateRqDto;
import ru.creditbank.common.library.dto.credit.rs.CreditCreateRsDto;
import ru.creditbank.common.library.dto.credit.rs.StatusUpdateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;
import ru.creditbank.credit.operations.service.CreditService;

import java.util.UUID;

@RestController
@RequestMapping("/credit-service/api/v1/credits/")
@RequiredArgsConstructor
@Slf4j
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/create")
    public CreditCreateRsDto create(@Valid @RequestBody CreditCreateRqDto rqDto) {
        return creditService.create(rqDto);
    }

    @GetMapping("/info/{creditId}")
    public CreditInfoRsDto info(@PathVariable("creditId") UUID creditId) {
        return creditService.getInfo(creditId);
    }

    @PatchMapping("/status/update/{creditId}")
    public StatusUpdateRsDto statusUpdate(
            @Valid @RequestBody StatusUpdateRqDto statusUpdateRqDto,
            @PathVariable("creditId") UUID creditId) {
        return creditService.statusUpdate(creditId, statusUpdateRqDto);
    }
}
