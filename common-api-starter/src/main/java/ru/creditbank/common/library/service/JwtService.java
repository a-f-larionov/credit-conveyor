package ru.creditbank.common.library.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.creditbank.common.library.exception.WrongOrInvalidJwtTokenException;
import ru.creditbank.common.library.jwt.JwtUser;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String HEADER_NAME = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    public static final String CLAIMS_FIELD_NAME_USER_ID = "user_id";
    public static final String CLAIMS_FIELD_NAME_USERNAME = "username";
    public static final String CLAIMS_FIELD_NAME_ROLES = "roles";

    @Value("${jwt.signing.key}")
    String jwtSigningKey;

    @Value("${jwt.signing.expiration_sec}")
    Long jwtSigningExpirationSec;

    public String generateToken(UserDetails userDetails, String userId) {

        return Jwts.builder()
                .claims(generateClaims(userDetails, userId))
                .subject(String.valueOf(userDetails.getUsername()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtSigningExpirationSec * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return
                    claims.get(CLAIMS_FIELD_NAME_USER_ID) != null &&
                            claims.get(CLAIMS_FIELD_NAME_USERNAME) != null &&
                            claims.get(CLAIMS_FIELD_NAME_ROLES) != null &&
                            !isTokenExpired(token);
        } catch (Exception e) {
            throw new WrongOrInvalidJwtTokenException("Token invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    public boolean isTokenUserValid(String token, UserDetails serverUser) {
        var tokenUser = extractUserDetailFromToken(token);
        return tokenUser != null && serverUser != null &&
                tokenUser.getUsername().equals(serverUser.getUsername());
    }

    public String extractUsername(String token) {
        return extractField(token, CLAIMS_FIELD_NAME_USERNAME, String.class);
    }

    public String extractUserId(String token) {
        return extractField(token, CLAIMS_FIELD_NAME_USER_ID, String.class);
    }

    public String extractRoles(String token) {
        return extractField(token, CLAIMS_FIELD_NAME_ROLES, String.class);
    }

    public JwtUser extractUserDetailFromToken(String jwtToken) {
        return JwtUser.builder()
                .id(UUID.fromString(extractUserId(jwtToken)))
                .username(extractUsername(jwtToken))
                .userRoles(Set.of(extractRoles(jwtToken).split(",")))
                .build();
    }

    public String resolveJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader(HEADER_NAME);
        if (!isBearerHeader(authHeader)) {
            return null;
        }

        var jwtToken = authHeader.substring(BEARER_PREFIX.length());
        if (jwtToken.isEmpty()) {
            throw new WrongOrInvalidJwtTokenException("Empty JWT Token", UNAUTHORIZED);
        }
        return jwtToken;
    }

    public boolean isBearerHeader(String authHeader) {
        return authHeader != null && authHeader.startsWith(BEARER_PREFIX);
    }

    private Map<String, ?> generateClaims(UserDetails userDetails, String userId) {
        Map<String, Object> claims = new HashMap<>();

        claims.put(CLAIMS_FIELD_NAME_USER_ID, userId);
        claims.put(CLAIMS_FIELD_NAME_USERNAME, userDetails.getUsername());
        var userRoles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put(CLAIMS_FIELD_NAME_ROLES, userRoles);

        return claims;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractField(String token, String fieldName, Class<T> targetType) {
        return extractClaim(token, claims -> claims.get(fieldName, targetType));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
