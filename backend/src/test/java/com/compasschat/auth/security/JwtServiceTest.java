package com.compasschat.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    // 32+ byte secret required for HS256
    private static final String SECRET = "test-secret-key-must-be-32-chars!";
    private static final long EXPIRATION_MS = 3_600_000L; // 1 hour

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS);
    }

    @Test
    void shouldGenerateNonNullToken_whenGenerateTokenCalled() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", "MEMBER");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void shouldReturnTrue_whenIsValidWithFreshToken() {
        String token = jwtService.generateToken(UUID.randomUUID(), "alice", "MEMBER");
        assertTrue(jwtService.isValid(token));
    }

    @Test
    void shouldReturnFalse_whenIsValidWithGarbageToken() {
        assertFalse(jwtService.isValid("not.a.jwt"));
    }

    @Test
    void shouldReturnFalse_whenIsValidWithEmptyString() {
        assertFalse(jwtService.isValid(""));
    }

    @Test
    void shouldExtractUsername_whenGetUsernameFromValidToken() {
        String token = jwtService.generateToken(UUID.randomUUID(), "alice", "MEMBER");
        assertEquals("alice", jwtService.getUsername(token));
    }

    @Test
    void shouldExtractUserId_whenGetUserIdFromValidToken() {
        UUID userId = UUID.randomUUID();
        String token = jwtService.generateToken(userId, "alice", "MEMBER");
        assertEquals(userId, jwtService.getUserId(token));
    }

    @Test
    void shouldExtractRole_whenGetRoleFromValidToken() {
        String token = jwtService.generateToken(UUID.randomUUID(), "alice", "ADMIN");
        assertEquals("ADMIN", jwtService.getRole(token));
    }

    @Test
    void shouldReturnFalse_whenIsValidWithExpiredToken() {
        JwtService shortLived = new JwtService(SECRET, 1L); // expires in 1 ms
        String token = shortLived.generateToken(UUID.randomUUID(), "alice", "MEMBER");
        // Sleep briefly to let it expire
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        assertFalse(shortLived.isValid(token));
    }
}
