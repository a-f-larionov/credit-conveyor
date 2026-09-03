package ru.creditbank.apigateway.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
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

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
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
                        .requestMatchers("/api/v1/user/info").hasAnyRole("USER", "ADMIN", "CREDIT_MANAGER")
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("Authentication error: {}", authException.getMessage());
                            errorResponseWriter.sendError(request, response, HttpStatus.UNAUTHORIZED);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("Access denied: {}", accessDeniedException.getMessage());
                            errorResponseWriter.sendError(request, response, HttpStatus.FORBIDDEN);
                        })
                )
                .addFilterBefore(jwtGatewayFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}
