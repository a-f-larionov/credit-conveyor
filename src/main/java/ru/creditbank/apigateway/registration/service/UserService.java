package ru.creditbank.apigateway.registration.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.core.UserModel;
import ru.creditbank.apigateway.exceptions.UserAlreadyExistsException;
import ru.creditbank.apigateway.exceptions.UserDoesNotExistsException;
import ru.creditbank.apigateway.exceptions.WrongPasswordException;
import ru.creditbank.apigateway.jwt.service.JWTService;
import ru.creditbank.apigateway.repositories.UsersReposity;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UsersReposity userReposity;
    private final JWTService jwtService;
    private final TokenStoreService tokenStoreService;

    public void register(UserModel user, String password) {

        user.setPasswordHash(passwordEncoder.encode(password));

        if (userReposity.existsByEmail(user.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        userReposity.save(user);
    }

    public String login(String email, String password) {

        var user = userReposity.findByEmail(email)
                .orElseThrow(UserDoesNotExistsException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongPasswordException();
        }

        var token = jwtService.generateToken(user);

        tokenStoreService.store(user, token);

        return token;
    }
}