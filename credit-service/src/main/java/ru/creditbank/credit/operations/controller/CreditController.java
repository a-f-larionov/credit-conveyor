package ru.creditbank.credit.operations.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rq.CreditInfoRqDto;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.dto.rs.CreditInfoRsDto;
import ru.creditbank.credit.operations.service.CreditService;

@RestController
@RequestMapping("/credit-service/api/v1/")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public CreditCreateRsDto create(@Valid @RequestBody CreditCreateRqDto rqDto) {

        return creditService.create(rqDto);
    }

    @PostMapping("/info")
    public CreditInfoRsDto info(@Valid @RequestBody CreditInfoRqDto rqDto) {

        return creditService.getInfo(rqDto);
    }
}
