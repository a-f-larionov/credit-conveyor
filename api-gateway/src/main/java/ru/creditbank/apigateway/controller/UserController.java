package ru.creditbank.apigateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.apigateway.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;
import ru.creditbank.apigateway.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/")
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/info")
    public UserInfoRsDto info(@Valid @RequestBody UserInfoRqDto rqDto) {
        log.info("Fetching user info by email: {}", rqDto.email());
        return userService.getInfo(rqDto);
    }
}
