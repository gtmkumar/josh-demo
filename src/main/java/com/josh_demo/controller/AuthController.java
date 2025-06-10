package com.josh_demo.controller;

import com.josh_demo.dto.request.AuthRequest;
import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.dto.response.AuthResponse;
import com.josh_demo.dto.response.UserResponseDto;
import com.josh_demo.service.AuthService;
import com.josh_demo.utility.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponseDto>> register(@RequestBody UserRequestDto request) {
        ApiResponse<UserResponseDto> response = authService.register(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        ApiResponse<AuthResponse> response = authService.login(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
} 