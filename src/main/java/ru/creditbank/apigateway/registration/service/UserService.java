package ru.creditbank.apigateway.registration.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.core.UserModel;
import ru.creditbank.apigateway.core.UserRole;
import ru.creditbank.apigateway.exceptions.UserAlreadyExistsException;
import ru.creditbank.apigateway.exceptions.UserDoesNotExistsException;
import ru.creditbank.apigateway.exceptions.WrongPasswordException;
import ru.creditbank.apigateway.jwt.service.JwtService;
import ru.creditbank.apigateway.registration.dto.FullNameDto;
import ru.creditbank.apigateway.registration.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.registration.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.registration.dto.rs.UserInfoRsDto;
import ru.creditbank.apigateway.repositories.UsersRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository userRepository;
    private final JwtService jwtService;
    private final TokenStoreService tokenStoreService;

    public void register(RegisterRqDto rqDto) {

        var user = UserService.mapRegisterRqDtoToUserModel(rqDto);

        user.setPasswordHash(passwordEncoder.encode(rqDto.getPassword()));

        if (userRepository.existsByEmail(user.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        userRepository.save(user);
    }

    public String login(String email, String password) {

        var user = userRepository.findByEmail(email)
                .orElseThrow(UserDoesNotExistsException::new);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongPasswordException();
        }

        var token = jwtService.generateToken(user);

        tokenStoreService.store(user, token);

        return token;
    }

    public UserInfoRsDto getUserByEmail(UserInfoRqDto rqDto) {
        return mapUserToUserInfoRsDto(userRepository.findByEmail(rqDto.getEmail())
                .orElseThrow(UserDoesNotExistsException::new));
    }

    public static UserModel mapRegisterRqDtoToUserModel(RegisterRqDto rqDto) {
        return UserModel.builder()
                .firstName(rqDto.getFullName().getFirstName())
                .lastName(rqDto.getFullName().getLastName())
                .middleName(rqDto.getFullName().getMiddleName())
                .email(rqDto.getEmail())
                .roles(Set.of(UserRole.ROLE_USER))
                .build();
    }

    public static UserInfoRsDto mapUserToUserInfoRsDto(UserModel user) {
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