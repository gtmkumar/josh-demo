package com.josh_demo.service.impl;

import com.josh_demo.model.Token;
import com.josh_demo.model.User;
import com.josh_demo.repository.TokenRepository;
import com.josh_demo.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Token saveToken(User user, String tokenValue, String tokenType) {
        Token token = new Token();
        token.setUser(user);
        token.setTokenValue(tokenValue);
        token.setTokenType(tokenType);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpirationTime(LocalDateTime.now().plusHours(24)); // 24 hours expiration
        token.setIsUsed(false);
        return tokenRepository.save(token);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void invalidateToken(String tokenValue) {
        tokenRepository.findByTokenValue(tokenValue)
                .ifPresent(token -> {
                    token.setIsUsed(true);
                    tokenRepository.save(token);
                });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void invalidateAllUserTokens(Long userId) {
        tokenRepository.deleteByUser_Id(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenValid(String tokenValue) {
        return tokenRepository.findByTokenValue(tokenValue)
                .map(token -> !token.getIsUsed() && token.getExpirationTime().isAfter(LocalDateTime.now()))
                .orElse(false);
    }
} 