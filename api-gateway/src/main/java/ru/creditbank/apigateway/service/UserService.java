package ru.creditbank.apigateway.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.creditbank.apigateway.entitiy.UserModel;
import ru.creditbank.apigateway.entitiy.UserRole;
import ru.creditbank.apigateway.exception.UserAlreadyExistsException;
import ru.creditbank.apigateway.exception.UserDoesNotExistsException;
import ru.creditbank.apigateway.exception.WrongPasswordException;
import ru.creditbank.apigateway.jwt.service.JwtService;
import ru.creditbank.apigateway.dto.FullNameDto;
import ru.creditbank.apigateway.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;
import ru.creditbank.apigateway.repository.UsersRepository;

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

        try {
            var user = UserService.mapRegisterRqDtoToUserModel(rqDto);

            user.setPasswordHash(passwordEncoder.encode(rqDto.getPassword()));

            if (userRepository.existsByEmail(user.getUsername())) {
                throw new UserAlreadyExistsException();
            }

            userRepository.save(user);
        } catch (UserAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }

    public LoginRsDto login(LoginRqDto rqDto) {

        try {
            var user = userRepository.findByEmail(rqDto.getEmail())
                    .orElseThrow(UserDoesNotExistsException::new);

            if (!passwordEncoder.matches(rqDto.getPassword(), user.getPassword())) {
                throw new WrongPasswordException();
            }

            var token = jwtService.generateToken(user);

            tokenStoreService.store(user, token);

            return LoginRsDto.builder().token(token).build();

        } catch (UserDoesNotExistsException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exists");

        } catch (WrongPasswordException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }
    }

    public UserInfoRsDto getUserByEmail(UserInfoRqDto rqDto) {
        return mapUserToUserInfoRsDto(getUserByEmail(rqDto.getEmail()));
    }

    public UserModel getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserDoesNotExistsException::new);
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