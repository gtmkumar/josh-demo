package com.josh_demo.service.impl;

import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.dto.request.validation.UserRequestValidator;
import com.josh_demo.dto.response.UserResponseDto;
import com.josh_demo.mapper.UserMapper;
import com.josh_demo.model.*;
import com.josh_demo.repository.*;
import com.josh_demo.service.UserService;
import com.josh_demo.utility.ApiHttpStatus;
import com.josh_demo.utility.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRequestValidator userRequestValidator;

    @Override
    @Transactional
    public ApiResponse<UserResponseDto> save(UserRequestDto userDto) {
        userRequestValidator.validate(userDto);

        User user = userMapper.toUser(userDto);
        UserProfile profile = userMapper.toUserProfile(userDto);
        profile.setUser(user);
        user.setProfile(profile);

        user.setPasswordHash(passwordEncoder.encode(userDto.getPassword()));

        Role role = getOrCreateRole(userDto.getRole());
        User savedUser = userRepository.save(user);

        UserRole userRole = createOrGetUserRole(savedUser, role);
        if (!savedUser.getUserRoles().contains(userRole)) {
            savedUser.getUserRoles().add(userRole);
        }

        UserResponseDto response = userMapper.toUserResponseDto(savedUser, profile);
        response.setRole(role.getName());
        return new ApiResponse<>(ApiHttpStatus.CREATED, response);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRoleRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });
    }

    private UserRole createOrGetUserRole(User user, Role role) {
        UserRoleId userRoleId = new UserRoleId(user.getId(), role.getId());
        return userRoleRepository.findById(userRoleId)
                .orElseGet(() -> {
                    UserRole newUserRole = new UserRole();
                    newUserRole.setUserRoleId(userRoleId);
                    newUserRole.setUser(user);
                    newUserRole.setRole(role);
                    return userRoleRepository.save(newUserRole);
                });
    }
}