package ru.creditbank.apigateway.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.dto.rq.LoginRqDto;
import ru.creditbank.apigateway.dto.rq.RegisterRqDto;
import ru.creditbank.apigateway.dto.rq.UserInfoRqDto;
import ru.creditbank.apigateway.dto.rs.LoginRsDto;
import ru.creditbank.apigateway.dto.rs.UserInfoRsDto;
import ru.creditbank.apigateway.entitiy.UserRole;
import ru.creditbank.apigateway.exception.UserAlreadyExistsException;
import ru.creditbank.apigateway.exception.UserDoesNotExistsException;
import ru.creditbank.apigateway.exception.WrongPasswordException;
import ru.creditbank.apigateway.jwt.JwtStore;
import ru.creditbank.apigateway.mapper.AuthMapper;
import ru.creditbank.apigateway.repository.UserRepository;

import java.util.Set;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtStore jwtStore;
    private final AuthMapper authMapper;

    @Transactional
    public void register(RegisterRqDto rqDto) {

        userRepository.findByEmail(rqDto.email())
                .ifPresent((user) -> {
                    throw new UserAlreadyExistsException("User already exists", CONFLICT);
                });

        var user = authMapper.mapRegisterRqDtoToUserEntity(rqDto, Set.of(UserRole.ROLE_USER));
        user.setPasswordHash(passwordEncoder.encode(rqDto.password()));

        userRepository.save(user);
    }

    @Transactional
    public LoginRsDto login(LoginRqDto rqDto) {

        var user = userRepository.findByEmail(rqDto.email())
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exists", NOT_FOUND));

        if (!passwordEncoder.matches(rqDto.password(), user.getPassword())) {
            throw new WrongPasswordException("Wrong password", UNAUTHORIZED);
        }

        var token = jwtService.generateToken(user, user.getId().toString());
        jwtStore.store(token, user);

        return authMapper.toLoginRsDto(token);
    }

    @Transactional
    public UserInfoRsDto getInfo(UserInfoRqDto rqDto) {
        var user = userRepository.findByEmail(rqDto.email())
                .orElseThrow(() -> new UserDoesNotExistsException("User does not exits", NOT_FOUND));
        return authMapper.mapUserToUserInfoRsDto(user);
    }
}