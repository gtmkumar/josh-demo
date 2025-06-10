package com.josh_demo.dto.request.validation;

import com.josh_demo.dto.request.AuthRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthRequestValidator {

    public void validate(AuthRequest request) {
        if (!request.isValid()) {
            throw new IllegalArgumentException(String.join("; ", request.validate()));
        }
    }
} 