package ru.creditbank.apigateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.apigateway.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public LoginRsDto login(@Valid @RequestBody LoginRqDto rqDto) {
        log.info("Try to login by email: {}", rqDto.email());
        return userService.login(rqDto);
    }
}
