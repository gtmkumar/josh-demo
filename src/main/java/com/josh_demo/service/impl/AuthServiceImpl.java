package com.josh_demo.service.impl;

import com.josh_demo.dto.request.AuthRequest;
import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.dto.request.validation.AuthRequestValidator;
import com.josh_demo.dto.request.validation.UserRequestValidator;
import com.josh_demo.dto.response.AuthResponse;
import com.josh_demo.dto.response.UserResponseDto;
import com.josh_demo.mapper.UserMapper;
import com.josh_demo.model.Role;
import com.josh_demo.model.User;
import com.josh_demo.model.UserProfile;
import com.josh_demo.model.UserRole;
import com.josh_demo.repository.RoleRepository;
import com.josh_demo.repository.UserRepository;
import com.josh_demo.repository.UserRoleRepository;
import com.josh_demo.security.JwtService;
import com.josh_demo.service.AuthService;
import com.josh_demo.utility.ApiHttpStatus;
import com.josh_demo.utility.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final UserRequestValidator userRequestValidator;
    private final AuthRequestValidator authRequestValidator;

    @Override
    @Transactional
    public ApiResponse<UserResponseDto> register(UserRequestDto request) {
        userRequestValidator.validate(request);

        User user = userMapper.toUser(request);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        UserProfile profile = userMapper.toUserProfile(request);
        profile.setUser(user);
        user.setProfile(profile);

        Role role = getOrCreateRole(request.getRole());
        User savedUser = userRepository.save(user);

        UserRole userRole = createUserRole(savedUser, role);
        savedUser.getUserRoles().add(userRole);

        UserResponseDto response = userMapper.toUserResponseDto(savedUser, profile);
        response.setRole(role.getName());

        return new ApiResponse<>(ApiHttpStatus.CREATED, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AuthResponse> login(AuthRequest request) {
        authRequestValidator.validate(request);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);

        User user = userRepository.findByUserName(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserResponseDto userResponse = userMapper.toUserResponseDto(user, user.getProfile());
        userResponse.setRole(roles.get(0));

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();

        return new ApiResponse<>(ApiHttpStatus.OK, response);
    }

    private Role getOrCreateRole(String roleName) {
        final String normalizedRoleName = roleName.startsWith("ROLE_")
                ? roleName
                : "ROLE_" + roleName.toUpperCase();

        return roleRepository.findByName(normalizedRoleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(normalizedRoleName);
                    return roleRepository.save(newRole);
                });
    }

    private UserRole createUserRole(User user, Role role) {
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        return userRoleRepository.save(userRole);
    }
}