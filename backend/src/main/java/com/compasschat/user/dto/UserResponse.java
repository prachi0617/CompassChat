package com.compasschat.user.dto;

import com.compasschat.user.User;
import com.compasschat.common.enums.Role;

import java.util.UUID;

public record UserResponse(UUID id, String username, String email, Role role) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
