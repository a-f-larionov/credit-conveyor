package ru.creditbank.common.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.creditbank.common.library.jwt.JwtUserDetails;

@Component
@RequiredArgsConstructor
public class JwtSecurityContextService {

    public void setSecurityContextAuthentication(JwtUserDetails userDetails) {

        var auth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
