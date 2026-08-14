package ru.creditbank.apigateway.registration.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.core.UserModel;
import ru.creditbank.apigateway.exceptions.UserAlreadyExistsException;
import ru.creditbank.apigateway.repositories.UsersReposity;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UsersReposity userReposity;

    public void register(UserModel user, String password) {

        user.setPasswordHash(passwordEncoder.encode(password));

        if (userReposity.existsByEmail(user.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        userReposity.save(user);
    }
}