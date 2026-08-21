package ru.creditbank.apigateway.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.creditbank.apigateway.dto.FullNameDto;
import ru.creditbank.apigateway.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;
import ru.creditbank.apigateway.entitiy.UserEntity;
import ru.creditbank.apigateway.entitiy.UserRole;
import ru.creditbank.apigateway.exception.UserAlreadyExistsException;
import ru.creditbank.apigateway.exception.UserDoesNotExistsException;
import ru.creditbank.apigateway.exception.WrongPasswordException;
import ru.creditbank.apigateway.jwt.JwtStore;
import ru.creditbank.apigateway.repository.UserRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtStore jwtStore;

    @Transactional
    public void register(RegisterRqDto rqDto) {

        try {
            var user = mapRegisterRqDtoToUserModel(rqDto);

            user.setPasswordHash(passwordEncoder.encode(rqDto.password()));

            if (userRepository.existsByEmail(user.getUsername())) {
                throw new UserAlreadyExistsException();
            }

            userRepository.save(user);
        } catch (UserAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }

    @Transactional
    public LoginRsDto login(LoginRqDto rqDto) {

        try {
            var user = userRepository.findByEmail(rqDto.email())
                    .orElseThrow(UserDoesNotExistsException::new);

            if (!passwordEncoder.matches(rqDto.password(), user.getPassword())) {
                throw new WrongPasswordException();
            }

            var token = jwtService.generateToken(user, user.getId().toString());
            jwtStore.store(token, user);

            return LoginRsDto.builder().token(token).build();

        } catch (UserDoesNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");

        } catch (WrongPasswordException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }
    }

    @Transactional
    public UserInfoRsDto getUserByEmail(UserInfoRqDto rqDto) {
        return mapUserToUserInfoRsDto(getUserByEmail(rqDto.email()));
    }

    private UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserDoesNotExistsException::new);
    }

    private static UserEntity mapRegisterRqDtoToUserModel(RegisterRqDto rqDto) {
        return UserEntity.builder()
                .firstName(rqDto.fullName().firstName())
                .lastName(rqDto.fullName().lastName())
                .middleName(rqDto.fullName().middleName())
                .email(rqDto.email())
                .roles(Set.of(UserRole.ROLE_USER))
                .build();
    }

    private static UserInfoRsDto mapUserToUserInfoRsDto(UserEntity user) {
        return UserInfoRsDto.builder()
                .email(user.getEmail())
                .fullName(FullNameDto.builder()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .middleName(user.getMiddleName())
                        .build()
                )
                .build();
    }
}