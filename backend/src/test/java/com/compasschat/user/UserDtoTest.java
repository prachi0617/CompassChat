package com.compasschat.user;

import com.compasschat.common.enums.Role;
import com.compasschat.user.dto.UserResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {

    @Test
    void shouldMapUserToUserResponse_whenFromCalled() {
        User user = new User("alice", "hash", Role.MEMBER);

        UserResponse response = UserResponse.from(user);

        assertEquals("alice", response.username());
        assertEquals(Role.MEMBER, response.role());
        assertNull(response.email());
        assertNull(response.assignedSubProject());
    }

    @Test
    void shouldIncludeEmail_whenUserHasEmail() {
        User user = new User("bob", "hash", Role.ADMIN);
        user.setEmail("bob@example.com");

        UserResponse response = UserResponse.from(user);

        assertEquals("bob@example.com", response.email());
        assertEquals(Role.ADMIN, response.role());
    }
}
