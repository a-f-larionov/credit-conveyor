package ru.creditbank.apigateway.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.creditbank.apigateway.config.ErrorResponseWriter;
import ru.creditbank.apigateway.config.SecurityConfig;
import ru.creditbank.apigateway.exception.WrongOrInvalidJwtTokenException;
import ru.creditbank.apigateway.service.JwtService;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
@RequiredArgsConstructor
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
        } catch (Exception e) {
            errorResponseWriter.sendError(response, HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void doFilter(HttpServletRequest request) {

        if (isPublicPath(request)) {
            return;
        }

        var jwtToken = resolveJwtToken(request);
        if (jwtToken == null) return;

        if (!jwtService.isTokenValid(jwtToken)) {
            throw new WrongOrInvalidJwtTokenException("Token Invalid");
        }

        setSecurityContextAuthentication(jwtToken);
    }

    private void setSecurityContextAuthentication(String jwtToken) {
        var userDetails = jwtService.getUserDetailsByToken(jwtToken);

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
            throw new WrongOrInvalidJwtTokenException("Empty JWT Token");
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
