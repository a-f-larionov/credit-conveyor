package ru.creditbank.credit.operations;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.creditbank.credit.operations.jwt.JwtUserDetails;
import ru.creditbank.credit.operations.service.JwtService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TestJwtGenerator {

    @Value("${jwt.signing.key}")
    String jwtSigningKey;

    @Value("${jwt.signing.expiration_sec}")
    Long jwtSigningExpirationSec;

    public String generateDefault() {

        var userDetails = JwtUserDetails.builder()
                .userId(123L)
                .username("username@email.ru")
                .userRoles(Set.of("ROLE_USER"))
                .build();

        return generateToken(userDetails, String.valueOf(123L));
    }

    public String generateToken(UserDetails userDetails, String userId) {

        return Jwts.builder()
                .claims(generateClaims(userDetails, userId))
                .subject(String.valueOf(userDetails.getUsername()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtSigningExpirationSec * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    private Map<String, ?> generateClaims(UserDetails userDetails, String userId) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(JwtService.CLAIMS_FIELD_NAME_USER_ID, userId);
        claims.put(JwtService.CLAIMS_FIELD_NAME_USERNAME, userDetails.getUsername());
        var userRoles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put(JwtService.CLAIMS_FIELD_NAME_ROLES, userRoles);

        return claims;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
