package ru.creditbank.apigateway.service;

import org.springframework.stereotype.Service;
import ru.creditbank.apigateway.entitiy.UserModel;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenStoreService {

    private final ConcurrentHashMap<String, Long> store = new ConcurrentHashMap<>();

    public void store(UserModel userModel, String token) {
        store.put(token, userModel.getId());
    }

    public Long geUserIdByToken(String token) {
        return store.get(token);
    }
}