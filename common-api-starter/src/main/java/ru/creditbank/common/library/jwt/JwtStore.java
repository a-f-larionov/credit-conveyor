package ru.creditbank.common.library.jwt;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class JwtStore {

    private final ConcurrentHashMap<String, JwtUserDetails> store = new ConcurrentHashMap<>();

    public void store(String token, JwtUserDetails userDetails) {
        store.put(token, userDetails);
    }

    public JwtUserDetails getUserDetailsByToken(String token) {
        return store.get(token);
    }
}