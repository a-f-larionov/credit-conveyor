package ru.creditbank.common.library.jwt;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtUserDetails extends UserDetails {

    UUID getId();
}
