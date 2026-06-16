package com.compasschat.user;

import com.compasschat.common.base.exception.ResourceNotFoundException;
import com.compasschat.common.enums.Role;
import com.compasschat.user.dto.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepo;

    private UserService service;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new UserService(userRepo);
    }

    // --- findByUsername() ---

    @Test
    void shouldReturnUser_whenFindByUsernameAndUserExists() {
        User user = new User("alice", "hash", Role.MEMBER);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));

        User result = service.findByUsername("alice");

        assertSame(user, result);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenFindByUsernameAndUserMissing() {
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByUsername("ghost"));
    }

    // --- updateProfile() ---

    @Test
    void shouldUpdateEmail_whenUpdateProfileWithNewEmail() {
        User user = new User("alice", "hash", Role.MEMBER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        UpdateUserRequest req = new UpdateUserRequest(null, "alice@example.com");
        service.updateProfile(userId, req);

        assertEquals("alice@example.com", user.getEmail());
        verify(userRepo).save(user);
    }

    @Test
    void shouldThrowIllegalStateException_whenUpdateProfileWithUsernameThatIsTaken() {
        User user = new User("alice", "hash", Role.MEMBER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.existsByUsername("bob")).thenReturn(true);

        UpdateUserRequest req = new UpdateUserRequest("bob", null);

        assertThrows(IllegalStateException.class, () -> service.updateProfile(userId, req));
        verify(userRepo, never()).save(any());
    }

    @Test
    void shouldSkipUsernameCheck_whenUpdateProfileWithNullUsername() {
        User user = new User("alice", "hash", Role.MEMBER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);

        UpdateUserRequest req = new UpdateUserRequest(null, null);
        service.updateProfile(userId, req);

        verify(userRepo, never()).existsByUsername(any());
    }

    @Test
    void shouldSkipUsernameCheck_whenUpdateProfileWithBlankUsername() {
        User user = new User("alice", "hash", Role.MEMBER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);

        UpdateUserRequest req = new UpdateUserRequest("   ", null);
        service.updateProfile(userId, req);

        verify(userRepo, never()).existsByUsername(any());
    }

    @Test
    void shouldSkipEmailUpdate_whenUpdateProfileWithNullEmail() {
        User user = new User("alice", "hash", Role.MEMBER);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.save(any())).thenReturn(user);

        UpdateUserRequest req = new UpdateUserRequest(null, null);
        service.updateProfile(userId, req);

        assertNull(user.getEmail());
    }

    @Test
    void shouldThrowResourceNotFoundException_whenUpdateProfileAndUserMissing() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        UpdateUserRequest req = new UpdateUserRequest(null, "x@y.com");

        assertThrows(ResourceNotFoundException.class, () -> service.updateProfile(userId, req));
    }
}
