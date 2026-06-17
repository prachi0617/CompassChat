package com.compasschat.auth;

import com.compasschat.auth.dto.AuthResponse;
import com.compasschat.auth.dto.LoginRequest;
import com.compasschat.auth.dto.RegisterRequest;
import com.compasschat.auth.security.JwtService;
import com.compasschat.common.enums.Role;
import com.compasschat.user.User;
import com.compasschat.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private PasswordEncoder encoder;
    @Mock private JwtService jwtService;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(userRepo, encoder, jwtService);
    }

    // --- register() ---

    @Test
    void shouldReturnAuthResponse_whenRegisterWithNewUsername() {
        when(userRepo.findByUsername("alice")).thenReturn(Optional.empty());
        when(encoder.encode("secret")).thenReturn("hashed");
        User saved = new User("alice", "hashed", Role.MEMBER);
        when(userRepo.save(any(User.class))).thenReturn(saved);
        when(jwtService.generateToken(any(), eq("alice"), eq("MEMBER"))).thenReturn("jwt-token");

        AuthResponse response = service.register(new RegisterRequest("alice", "secret"));

        assertNotNull(response);
        assertEquals("alice", response.username());
        assertEquals("jwt-token", response.token());
        assertEquals("MEMBER", response.role());
        verify(encoder).encode("secret");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void shouldThrowIllegalStateException_whenRegisterWithTakenUsername() {
        User existing = new User("alice", "hash", Role.MEMBER);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class,
                () -> service.register(new RegisterRequest("alice", "password")));
        verify(userRepo, never()).save(any());
    }

    // --- login() ---

    @Test
    void shouldReturnAuthResponse_whenLoginWithCorrectCredentials() {
        User user = new User("alice", "hashed", Role.MEMBER);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(encoder.matches("secret", "hashed")).thenReturn(true);
        when(jwtService.generateToken(any(), eq("alice"), eq("MEMBER"))).thenReturn("jwt-token");

        AuthResponse response = service.login(new LoginRequest("alice", "secret"));

        assertNotNull(response);
        assertEquals("alice", response.username());
        assertEquals("jwt-token", response.token());
    }

    @Test
    void shouldThrowBadCredentialsException_whenLoginWithUnknownUsername() {
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("ghost", "pass")));
        verify(encoder, never()).matches(anyString(), anyString());
    }

    @Test
    void shouldThrowBadCredentialsException_whenLoginWithWrongPassword() {
        User user = new User("alice", "hashed", Role.MEMBER);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> service.login(new LoginRequest("alice", "wrong")));
        verify(jwtService, never()).generateToken(any(), anyString(), anyString());
    }
}
