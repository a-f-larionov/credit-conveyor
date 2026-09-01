package ru.creditbank.apigateway.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.creditbank.apigateway.config.SecurityConfig;
import ru.creditbank.common.library.config.ErrorResponseWriter;
import ru.creditbank.common.library.exception.BusinessException;
import ru.creditbank.common.library.exception.WrongOrInvalidJwtTokenException;
import ru.creditbank.common.library.service.JwtService;
import ru.creditbank.common.library.service.JwtSecurityContextService;
import ru.creditbank.common.library.jwt.JwtStore;

import java.io.IOException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtGatewayFilter extends OncePerRequestFilter {

    private final ErrorResponseWriter errorResponseWriter;
    private final JwtService jwtService;
    private final JwtStore jwtStore;
    private final JwtSecurityContextService jwtSecurityContextService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            doFilter(request);
        } catch (BusinessException e) {
            log.warn(e.getMessage());
            errorResponseWriter.sendError(request, response, UNAUTHORIZED, e.getMessage());
            return;
        } catch (Exception e) {
            log.error(e.getMessage());
            errorResponseWriter.sendError(request, response, INTERNAL_SERVER_ERROR);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void doFilter(HttpServletRequest request) {

        if (SecurityConfig.isPublicPath(request)) {
            return;
        }

        var jwtToken = jwtService.resolveJwtToken(request);
        if (jwtToken == null) {
            throw new WrongOrInvalidJwtTokenException("Token is empty", UNAUTHORIZED);
        }

        if (!jwtService.isTokenValid(jwtToken)) {
            throw new WrongOrInvalidJwtTokenException("Token invalid", UNAUTHORIZED);
        }

        var userDetails = jwtStore.getUserDetailsByToken(jwtToken);
        if (!jwtService.isTokenUserValid(jwtToken, userDetails)) {
            throw new WrongOrInvalidJwtTokenException("Token invalid", UNAUTHORIZED);
        }

        jwtSecurityContextService.setSecurityContextAuthentication(userDetails);
    }
}
