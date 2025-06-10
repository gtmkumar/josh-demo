package com.josh_demo.mapper;

import com.josh_demo.dto.request.UserRequestDto;
import com.josh_demo.dto.response.UserResponseDto;
import com.josh_demo.model.User;
import com.josh_demo.model.UserProfile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    // If you want to hash the password, do it in the service, not here.
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    User toUser(UserRequestDto dto);

    // Ensure no mapping tries to set "userId" on UserProfile; only ignore "user" property
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    UserProfile toUserProfile(UserRequestDto dto);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "createdAt", source = "user.createdAt")
    @Mapping(target = "role", expression = "java((user.getUserRoles() != null && !user.getUserRoles().isEmpty() && user.getUserRoles().iterator().next().getRole() != null) ? user.getUserRoles().iterator().next().getRole().getName() : null)")
    UserResponseDto toUserResponseDto(User user, UserProfile profile);

    @AfterMapping
    default void linkUserWithProfile(UserRequestDto dto, @MappingTarget User user) {
        UserProfile profile = toUserProfile(dto);
        profile.setUser(user);          // bi-directional link
        user.setProfile(profile);       // main link
    }
}
