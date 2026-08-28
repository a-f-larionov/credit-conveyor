package ru.creditbank.credit.operations.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.creditbank.common.library.config.ErrorResponseWriter;
import ru.creditbank.credit.operations.config.SecurityConfig;
import ru.creditbank.credit.operations.exception.BusinessException;
import ru.creditbank.credit.operations.exception.WrongOrInvalidJwtTokenException;
import ru.creditbank.credit.operations.service.JwtService;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpStatus.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final ErrorResponseWriter errorResponseWriter;
    private final JwtService jwtService;

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

        if (isPublicPath(request)) {
            return;
        }

        var jwtToken = resolveJwtToken(request);
        if (jwtToken == null) {
            throw new WrongOrInvalidJwtTokenException("Token is empty", UNAUTHORIZED);
        }

        if (!jwtService.isTokenValid(jwtToken)) {
            throw new WrongOrInvalidJwtTokenException("Token invalid", UNAUTHORIZED);
        }

        setSecurityContextAuthentication(jwtToken);
    }

    private void setSecurityContextAuthentication(String jwtToken) {
        var userDetails = jwtService.extractUserDetailFromToken(jwtToken);

        var auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String resolveJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_NAME);
        if (isNotBearer(authHeader)) {
            return null;
        }

        var jwtToken = authHeader.substring(BEARER_PREFIX.length());
        if (isEmpty(jwtToken)) {
            throw new WrongOrInvalidJwtTokenException("Empty JWT Token", UNAUTHORIZED);
        }
        return jwtToken;
    }

    private boolean isPublicPath(HttpServletRequest request) {
        return SecurityConfig.PUBLIC_URLS.contains(request.getRequestURI());
    }

    private boolean isNotBearer(String authHeader) {
        return isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX);
    }
}
