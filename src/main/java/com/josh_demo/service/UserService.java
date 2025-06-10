package com.josh_demo.service;

import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.dto.response.UserResponseDto;
import com.josh_demo.exception.DuplicateResourceException;
import com.josh_demo.model.*;
import com.josh_demo.repository.*;
import com.josh_demo.mapper.UserMapper;
import com.josh_demo.utility.ApiResponse;
import com.josh_demo.utility.ApiHttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       UserRoleRepository userRoleRepository,
                       UserMapper userMapper,
                       PasswordEncoder passwordEncoder, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public ApiResponse<UserResponseDto> save(UserRequestDto userDto) {
        //validate
        checkDuplicateUser(userDto);

        User user = userMapper.toUser(userDto);
        UserProfile profile = userMapper.toUserProfile(userDto);
        profile.setUser(user);
        user.setProfile(profile);

        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));

        // Handle Role
        Role role = roleRepository.findByName(userDto.getRole())
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(userDto.getRole());
                    return roleRepository.save(newRole);
                });

        // Save User first to get the ID
        User savedUser = userRepository.save(user);

        // Check if UserRole already exists
        UserRoleId userRoleId = new UserRoleId(savedUser.getId(), role.getId());
        Optional<UserRole> existingUserRole = userRoleRepository.findById(userRoleId);

        UserRole userRole;
        if (existingUserRole.isPresent()) {
            userRole = existingUserRole.get();
        } else {
            userRole = new UserRole();
            userRole.setUserRoleId(userRoleId);
            userRole.setUser(savedUser);
            userRole.setRole(role);
            userRole = userRoleRepository.save(userRole);
        }

        // Add to user's roles collection if not already present
        if (!savedUser.getUserRoles().contains(userRole)) {
            savedUser.getUserRoles().add(userRole);
        }

        // Use UserMapper for response mapping
        UserResponseDto response = userMapper.toUserResponseDto(savedUser, profile);
        return new ApiResponse<>(ApiHttpStatus.CREATED, response);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        // Delete user roles first
        userRoleRepository.deleteByUserId(id);
        // Then delete the user
        userRepository.deleteById(id);
    }
    private void checkDuplicateUser(UserRequestDto userDto) {
        List<String> errors = new ArrayList<>();

        if (userRepository.findByUserName(userDto.getUserName()).isPresent()) {
            errors.add("Username already exists");
        }

        if (userProfileRepository.existsByEmail(userDto.getEmail())) {
            errors.add("Email already exists");
        }

        if (userProfileRepository.existsByPhone(userDto.getPhone())) {
            errors.add("Phone number already exists");
        }

        if (!errors.isEmpty()) {
            throw new DuplicateResourceException(String.join("; ", errors));
        }
    }

}
