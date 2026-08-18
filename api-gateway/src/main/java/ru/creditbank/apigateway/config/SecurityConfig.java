package ru.creditbank.apigateway.config;

import jakarta.servlet.http.HttpServletResponse;
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
import ru.creditbank.apigateway.jwt.filter.JwtFilter;

import java.util.Set;

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

    private final JwtFilter jwtAuthenticationFilter;
    private final ErrorResponseWriter errorResponseWriter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(authz -> authz
                        .requestMatchers(PUBLIC_URLS.toArray(String[]::new)).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            errorResponseWriter.sendError(response, HttpServletResponse.SC_FORBIDDEN, "Unauthorized");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                errorResponseWriter.sendError(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden"))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}
