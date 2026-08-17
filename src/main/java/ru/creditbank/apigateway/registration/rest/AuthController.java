package ru.creditbank.apigateway.registration.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.creditbank.apigateway.exceptions.UserDoesNotExistsException;
import ru.creditbank.apigateway.exceptions.WrongPasswordException;
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

        try {
            var token = userService.login(rqDto.getEmail(), rqDto.getPassword());
            return new LoginRsDto(token);

        } catch (UserDoesNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");

        } catch (WrongPasswordException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }
    }
}
