package ru.creditbank.apigateway.registration.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.apigateway.registration.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.registration.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.registration.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public LoginRsDto login(@Valid @RequestBody LoginRqDto rqDto) {

        return userService.login(rqDto);
    }
}
