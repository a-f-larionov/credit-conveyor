package ru.creditbank.apigateway.registration.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.creditbank.apigateway.registration.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.registration.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class RegisterController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRqDto rqDto) {

            userService.register(rqDto);
    }
}
