package ru.creditbank.credit.operations.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credit-service/api/v1/")
public class CreditCreateController {

    @PostMapping("/credit")
    public void credit() {

    }
}
