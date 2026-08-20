package ru.creditbank.apigateway.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtStore {

    private final ConcurrentHashMap<String, UserDetails> store = new ConcurrentHashMap<>();

    public void store(String token, UserDetails userDetails) {
        store.put(token, userDetails);
    }

    public UserDetails getUserDetailsByToken(String token) {
        return store.get(token);
    }
}