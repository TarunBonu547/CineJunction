package com.cinejunction.mapper;

import com.cinejunction.dto.response.UserProfileResponse;
import com.cinejunction.entity.User;

public class UserMapper {

    public static UserProfileResponse toUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
