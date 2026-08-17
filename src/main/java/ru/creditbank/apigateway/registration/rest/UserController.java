package ru.creditbank.apigateway.registration.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.apigateway.registration.dto.FullNameDto;
import ru.creditbank.apigateway.registration.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.registration.dto.rs.UserInfoRsDto;
import ru.creditbank.apigateway.registration.service.UserService;


@RestController
@RequiredArgsConstructor
@Transactional
@RequestMapping("/api/v1/user/")
public class UserController {

    private final UserService userService;

    @PostMapping("/info")
    public UserInfoRsDto info(@Valid @RequestBody UserInfoRqDto rqDto) {

        var user = userService.getUserByEmail(rqDto.getEmail());

        return UserInfoRsDto.builder()
                .email(user.getEmail())
                .fullNameDto(FullNameDto.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .middleName(user.getMiddleName())
                        .build()
                )
                .build();
    }
}
