package ru.creditbank.apigateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class RegisterController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRqDto rqDto) {
        log.info("Register user: {}, {}", rqDto.email(), rqDto.fullName());
        userService.register(rqDto);
    }
}
