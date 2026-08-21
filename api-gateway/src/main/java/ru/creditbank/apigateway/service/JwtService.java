package ru.creditbank.apigateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.jwt.JwtStore;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String CLAIMS_FIELD_NAME_USER_ID = "user_id";
    private static final String CLAIMS_FIELD_NAME_USERNAME = "username";
    private static final String CLAIMS_FIELD_NAME_ROLES = "roles";

    @Value("${jwt.signing.key}")
    String jwtSigningKey;

    @Value("${jwt.signing.expiration_sec}")
    Long jwtSigningExpirationSec;

    private final JwtStore jwtStore;

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
        var userDetail = jwtStore.getUserDetailsByToken(token);
        return userDetail != null && userDetail.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractField(token, CLAIMS_FIELD_NAME_USERNAME, String.class);
    }

    public UserDetails getUserDetailsByToken(String jwtToken) {
        return jwtStore.getUserDetailsByToken(jwtToken);
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
        final Claims claims = extractAllClaims(token);
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
