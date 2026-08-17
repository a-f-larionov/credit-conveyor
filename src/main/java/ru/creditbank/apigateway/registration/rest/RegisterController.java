package ru.creditbank.apigateway.registration.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.creditbank.apigateway.core.UserModel;
import ru.creditbank.apigateway.core.UserRole;
import ru.creditbank.apigateway.exceptions.UserAlreadyExistsException;
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

        try {
            var user = UserModel.builder()
                    .firstName(rqDto.getFullName().getFirstName())
                    .lastName(rqDto.getFullName().getLastName())
                    .middleName(rqDto.getFullName().getMiddleName())
                    .email(rqDto.getEmail())
                    .userRole(UserRole.USER)
                    .build();

            userService.register(user, rqDto.getPassword());

        } catch (UserAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }
}
