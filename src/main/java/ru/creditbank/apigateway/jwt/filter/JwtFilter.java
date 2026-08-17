package ru.creditbank.apigateway.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.creditbank.apigateway.config.ErrorResponseWriter;
import ru.creditbank.apigateway.config.SecurityConfig;
import ru.creditbank.apigateway.core.UserModel;
import ru.creditbank.apigateway.exceptions.WrongOrInvalidJwtTokenException;
import ru.creditbank.apigateway.jwt.service.JwtService;
import ru.creditbank.apigateway.registration.service.UserService;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final UserService userService;
    private final JwtService jwtService;
    private final ErrorResponseWriter errorResponseWriter;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            doFilter(request);
        } catch (Exception e) {
            errorResponseWriter.sendError(response, HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void doFilter(HttpServletRequest request) {

        if (SecurityConfig.PUBLIC_URLS.contains(request.getRequestURI())) {
            return;
        }

        String authHeader = request.getHeader(HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            return;
        }

        var jwtToken = authHeader.substring(BEARER_PREFIX.length());
        if (StringUtils.isEmpty(jwtToken)) {
            throw new WrongOrInvalidJwtTokenException("Empty JWT Token");
        }

        var userEmail = jwtService.extractUserEmail(jwtToken);
        if (StringUtils.isEmpty(userEmail)) {
            throw new WrongOrInvalidJwtTokenException("Empty userEmail");
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }

        UserModel userModel = userService.getUserByEmail(userEmail);
        if (!jwtService.isTokenValid(jwtToken, userModel.getEmail())) {
            throw new WrongOrInvalidJwtTokenException("Token Invalid");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userModel,
                null,
                userModel.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
