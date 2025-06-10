package com.josh_demo.service;

import com.josh_demo.model.Token;
import com.josh_demo.model.User;

public interface TokenService {
    Token saveToken(User user, String tokenValue, String tokenType);
    void invalidateToken(String tokenValue);
    void invalidateAllUserTokens(Long userId);
    boolean isTokenValid(String tokenValue);
} 