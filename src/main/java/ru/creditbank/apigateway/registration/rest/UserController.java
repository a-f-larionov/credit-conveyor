package ru.creditbank.apigateway.registration.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.creditbank.apigateway.core.UserModel;
import ru.creditbank.apigateway.registration.dto.FullNameDto;
import ru.creditbank.apigateway.registration.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.registration.dto.rs.UserInfoRsDto;
import ru.creditbank.apigateway.registration.service.UserService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/")
public class UserController {

    private final UserService userService;

    @PostMapping("/info")
    @PreAuthorize("hasRole('USER')")
    public UserInfoRsDto info(@Valid @RequestBody UserInfoRqDto rqDto) {

        var user = userService.getUserByEmail(rqDto.getEmail());

        return mapUserToUserInfoRsDto(user);
    }

    @PostMapping("/info-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public UserInfoRsDto infoAdmin(@Valid @RequestBody UserInfoRqDto rqDto) {

        var user = userService.getUserByEmail(rqDto.getEmail());

        return mapUserToUserInfoRsDto(user);
    }

    private static UserInfoRsDto mapUserToUserInfoRsDto(UserModel user) {
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
