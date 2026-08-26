package ru.creditbank.credit.operations.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.creditbank.credit.operations.exception.WrongOrInvalidJwtTokenException;
import ru.creditbank.credit.operations.jwt.JwtUserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final String CLAIMS_FIELD_NAME_USER_ID = "user_id";
    public static final String CLAIMS_FIELD_NAME_USERNAME = "username";
    public static final String CLAIMS_FIELD_NAME_ROLES = "roles";

    @Value("${jwt.signing.key}")
    String jwtSigningKey;

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return
                    claims.get(CLAIMS_FIELD_NAME_USER_ID) != null &&
                            claims.get(CLAIMS_FIELD_NAME_USERNAME) != null &&
                            claims.get(CLAIMS_FIELD_NAME_ROLES) != null &&
                            !isTokenExpired(token);
        } catch (Exception e) {
            throw new WrongOrInvalidJwtTokenException("Token Invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    public UserDetails extractUserDetailFromToken(String jwtToken) {

        return JwtUserDetails.builder()
                .userId(UUID.fromString(extractUserId(jwtToken)))
                .username(extractUsername(jwtToken))
                .userRoles(Set.of(extractRoles(jwtToken).split(",")))
                .build();
    }

    public String extractUserId(String token) {
        return extractField(token, CLAIMS_FIELD_NAME_USER_ID, String.class);
    }

    public String extractUsername(String token) {
        return extractField(token, CLAIMS_FIELD_NAME_USERNAME, String.class);
    }

    public String extractRoles(String token) {
        return extractField(token, CLAIMS_FIELD_NAME_ROLES, String.class);
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
