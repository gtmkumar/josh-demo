package com.josh_demo.controller;

import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.dto.response.UserResponseDto;
import com.josh_demo.model.User;
import com.josh_demo.service.UserService;
import com.josh_demo.utility.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }


    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> create(@RequestBody UserRequestDto userDto) {
        ApiResponse<UserResponseDto> apiResponse = userService.save(userDto);
        return ResponseEntity.status(apiResponse.getStatusCode()).body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.deleteById(id);
    }
}
