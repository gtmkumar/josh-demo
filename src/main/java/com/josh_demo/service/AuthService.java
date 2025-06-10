package com.josh_demo.service;

import com.josh_demo.dto.request.AuthRequest;
import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.dto.response.AuthResponse;
import com.josh_demo.dto.response.UserResponseDto;
import com.josh_demo.exception.DuplicateResourceException;
import com.josh_demo.model.Role;
import com.josh_demo.model.User;
import com.josh_demo.model.UserProfile;
import com.josh_demo.model.UserRole;
import com.josh_demo.repository.RoleRepository;
import com.josh_demo.repository.UserProfileRepository;
import com.josh_demo.repository.UserRepository;
import com.josh_demo.repository.UserRoleRepository;
import com.josh_demo.security.JwtService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public ApiResponse<UserResponseDto> register(UserRequestDto request) {
        // Validate user
        checkDuplicateUser(request);

        // Create user
        User user = new User();
        user.setUserName(request.getUserName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);

        // Create profile
        UserProfile profile = new UserProfile();
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setEmail(request.getEmail());
        profile.setPhone(request.getPhone());
        profile.setUser(user);
        user.setProfile(profile);

        // Set role
        final String roleName = request.getRole().startsWith("ROLE_") 
            ? request.getRole() 
            : "ROLE_" + request.getRole().toUpperCase();
        
        Role role = roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(roleName);
                    return roleRepository.save(newRole);
                });

        // Save user
        User savedUser = userRepository.save(user);

        // Create user role
        UserRole userRole = new UserRole();
        userRole.setUser(savedUser);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        // Add role to user's roles collection
        savedUser.getUserRoles().add(userRole);

        // Create response
        UserResponseDto response = new UserResponseDto();
        response.setId(savedUser.getId());
        response.setUserName(savedUser.getUserName());
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setEmail(profile.getEmail());
        response.setPhone(profile.getPhone());
        response.setActive(savedUser.isActive());
        response.setRole(role.getName());

        return new ApiResponse<>(ApiHttpStatus.CREATED, response);
    }

    @Transactional(readOnly = true)
    public ApiResponse<AuthResponse> login(AuthRequest request) {
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

        // Get roles from UserDetails
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        UserResponseDto userResponse = new UserResponseDto();
        userResponse.setId(user.getId());
        userResponse.setUserName(user.getUserName());
        userResponse.setFirstName(user.getProfile().getFirstName());
        userResponse.setLastName(user.getProfile().getLastName());
        userResponse.setEmail(user.getProfile().getEmail());
        userResponse.setPhone(user.getProfile().getPhone());
        userResponse.setActive(user.isActive());
        // Set the first role (assuming one user has one primary role)
        userResponse.setRole(roles.get(0));

        AuthResponse response = AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();

        return new ApiResponse<>(ApiHttpStatus.OK, response);
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