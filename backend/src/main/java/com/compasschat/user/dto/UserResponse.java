package com.compasschat.user.dto;

import com.compasschat.common.enums.PresenceStatus;
import com.compasschat.common.enums.Role;
import com.compasschat.user.User;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        Role role,
        PresenceStatus presence,
        String assignedSubProject
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getPresence(),
                user.getAssignedSubProject()
        );
    }
}
