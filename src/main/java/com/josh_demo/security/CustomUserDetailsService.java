package com.josh_demo.security;

import com.josh_demo.model.User;
import com.josh_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> {
                    String roleName = userRole.getRole().getName();
                    // Ensure role name is uppercase and has ROLE_ prefix
                    if (!roleName.startsWith("ROLE_")) {
                        roleName = "ROLE_" + roleName.toUpperCase();
                    }
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPasswordHash(),
                user.isActive(),
                true,
                true,
                true,
                authorities
        );
    }
} 