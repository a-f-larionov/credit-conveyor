package ru.creditbank.loan.management;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.creditbank.common.library.enums.UserRole;
import ru.creditbank.common.library.jwt.JwtUser;
import ru.creditbank.common.library.service.JwtService;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestJwtGenerator {

    @Value("${jwt.signing.key}")
    String jwtSigningKey;

    @Value("${jwt.signing.expiration_sec}")
    Long jwtSigningExpirationSec;

    public String generate() {
        return generate(UUID.randomUUID());
    }

    public String generate(Set<UserRole> roles) {
        var userId = UUID.randomUUID();
        return generate(userId, "username" + userId + "@email.ru", roles);
    }

    public String generate(UUID userId) {
        return generate(userId, "username" + userId + "@email.ru");
    }

    public String generate(UUID userId, String userEmail) {
        return generate(userId, userEmail, Set.of(UserRole.ROLE_USER));
    }

    public String generate(UUID userId, String userEmail, Set<UserRole> userRoles) {

        var userDetails = JwtUser.builder()
                .id(userId)
                .username(userEmail)
                .userRoles(userRoles.stream().map(Enum::toString).collect(Collectors.toSet()))
                .build();

        return generateToken(userDetails, userDetails.getId().toString());
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
