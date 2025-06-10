package com.josh_demo.service;

import com.josh_demo.dto.request.AuthRequest;
import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.dto.response.AuthResponse;
import com.josh_demo.dto.response.UserResponseDto;
import com.josh_demo.utility.ApiResponse;

public interface AuthService {
    ApiResponse<UserResponseDto> register(UserRequestDto request);
    ApiResponse<AuthResponse> login(AuthRequest request);
}