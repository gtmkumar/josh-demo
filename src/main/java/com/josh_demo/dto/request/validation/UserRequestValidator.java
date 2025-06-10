package com.josh_demo.dto.request.validation;

import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.exception.DuplicateResourceException;
import com.josh_demo.repository.UserProfileRepository;
import com.josh_demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRequestValidator {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public void validate(UserRequestDto userDto) {
        // First validate the DTO itself
        List<String> errors = userDto.validate();
        
        // Then check for duplicates
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