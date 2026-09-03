package ru.creditbank.common.library.service;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.creditbank.common.library.enums.UserRole;
import ru.creditbank.common.library.jwt.JwtUserDetails;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SecurityService {

    public void checkAccess(String ownerUserName, UserRole... roles) {

        var auth = getAuthentication();

        if (!hasAnyRole(auth, roles) && !isOwnerByEmail(auth, ownerUserName)) {
            throw new AccessDeniedException("You can access only to own data");
        }
    }

    @NonNull
    private static Authentication getAuthentication() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User not authenticated");
        }
        return auth;
    }


    public void checkAccess(UUID userId, UserRole... roles) {

        var auth = getAuthentication();

        if (!hasAnyRole(auth, roles) && !isOwnerByUserId(auth, userId)) {
            throw new AccessDeniedException("You can access only to own data");
        }
    }


    private boolean isOwnerByEmail(Authentication authentication, String email) {
        return authentication.getName().equals(email);
    }

    private boolean isOwnerByUserId(Authentication authentication, UUID userId) {
        return ((JwtUserDetails) authentication.getPrincipal()).getId().equals(userId);
    }

    private boolean hasAnyRole(Authentication auth, UserRole... roles) {
        var authorityArray = authorityToStringArray(roles);
        var roleSet = Arrays.stream(authorityArray).collect(Collectors.toSet());
        return auth.getAuthorities().stream()
                .anyMatch(a -> roleSet.contains(a.getAuthority()));
    }

    public static String[] authorityToStringArray(UserRole... roles) {
        return Arrays.stream(roles).map(Enum::toString).toArray(String[]::new);
    }
}
