package ru.creditbank.apigateway.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.creditbank.apigateway.jwt.JwtGatewayFilter;
import ru.creditbank.common.library.config.ErrorResponseWriter;

import java.util.Set;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final Set<String> PUBLIC_URLS = Set.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/error"
    );

    private final JwtGatewayFilter jwtGatewayFilter;
    private final ErrorResponseWriter errorResponseWriter;

    public static boolean isPublicPath(HttpServletRequest request) {
        return SecurityConfig.PUBLIC_URLS.contains(request.getRequestURI());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS.toArray(String[]::new)).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                errorResponseWriter.sendError(request, response, UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                errorResponseWriter.sendError(request, response, FORBIDDEN))
                )
                .addFilterBefore(jwtGatewayFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}
