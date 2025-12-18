package com.fintrack.fintrack.mapper;

import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.dto.userDTO.RegisterUserRequest;
import com.fintrack.fintrack.dto.userDTO.UserResponse;
import com.fintrack.fintrack.dto.userDTO.AuthResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);

    AuthResponse toAuthResponse(User user, String token);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterUserRequest request);
}
