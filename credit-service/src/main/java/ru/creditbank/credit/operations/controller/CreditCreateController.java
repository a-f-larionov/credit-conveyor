package ru.creditbank.credit.operations.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.credit.operations.dto.rq.CreditCreateRqDto;
import ru.creditbank.credit.operations.dto.rs.CreditCreateRsDto;
import ru.creditbank.credit.operations.service.CreditCreateService;

@RestController
@RequestMapping("/credit-service/api/v1/")
@RequiredArgsConstructor
public class CreditCreateController {

    private final CreditCreateService creditCreateService;

    @PostMapping("/create")
    public CreditCreateRsDto create(CreditCreateRqDto rqDto) {

        return creditCreateService.create(rqDto);
    }
}
