package com.josh_demo.dto.response;

import lombok.Data;
import java.time.Instant;

@Data
public class UserResponseDto {
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private boolean isActive;
    private Instant lastLogin;
    private Instant createdAt;
    private String role;
}
