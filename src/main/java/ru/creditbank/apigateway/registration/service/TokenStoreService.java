package ru.creditbank.apigateway.registration.service;

import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.core.UserModel;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenStoreService {

    private final ConcurrentHashMap<String, Integer> store = new ConcurrentHashMap<>();

    public void store(UserModel userModel, String token) {
        store.put(token, userModel.getId());
    }

    public Integer geUserIdByToken(String token) {
        return store.get(token);
    }
}