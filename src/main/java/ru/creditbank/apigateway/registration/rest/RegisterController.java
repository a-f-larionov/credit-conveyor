package ru.creditbank.apigateway.registration.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.creditbank.apigateway.core.UserRole;
import ru.creditbank.apigateway.core.UserModel;
import ru.creditbank.apigateway.exceptions.UserAlreadyExistsException;
import ru.creditbank.apigateway.registration.dto.RegisterRqDTO;
import ru.creditbank.apigateway.registration.service.UserService;

@RestController
@RequiredArgsConstructor
@Transactional
public class RegisterController {

    private final UserService userService;

    @PostMapping("/api/v1/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRqDTO rqDto) {

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
