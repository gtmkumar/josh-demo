package com.josh_demo.service;

import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.dto.response.UserResponseDto;
import com.josh_demo.model.*;
import com.josh_demo.utility.ApiResponse;

import java.util.List;

public interface UserService {
    ApiResponse<UserResponseDto> save(UserRequestDto userDto);
    List<User> findAll();
    void deleteById(Long id);
}