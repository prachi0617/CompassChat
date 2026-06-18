package com.compasschat.ai.escalation;

import com.compasschat.ai.dto.EscalateResponse;
import com.compasschat.common.enums.Role;
import com.compasschat.user.User;
import com.compasschat.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EscalationServiceTest {

    @Mock private EscalationLogRepository logs;
    @Mock private UserRepository users;

    private EscalationService service;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new EscalationService(logs, users);
    }

    @Test
    void shouldSaveLogAndReturnAdminUser_whenAdminExists() {
        User admin = new User("admin_user", "hash", Role.ADMIN);
        when(users.findAll()).thenReturn(List.of(admin));
        when(logs.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EscalateResponse response = service.escalate(userId, "need help");

        assertEquals("admin_user", response.adminUsername());
        assertEquals("Admin Team", response.dmChannelHint());
        verify(logs).save(any(EscalationLog.class));
    }

    @Test
    void shouldSaveLogAndReturnModeratorUser_whenModeratorExistsButNoAdmin() {
        User mod = new User("mod_user", "hash", Role.MODERATOR);
        when(users.findAll()).thenReturn(List.of(mod));
        when(logs.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EscalateResponse response = service.escalate(userId, "need help");

        assertEquals("mod_user", response.adminUsername());
    }

    @Test
    void shouldReturnFallbackResponse_whenNoAdminOrModeratorExists() {
        User member = new User("regular_user", "hash", Role.MEMBER);
        when(users.findAll()).thenReturn(List.of(member));
        when(logs.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EscalateResponse response = service.escalate(userId, "need help");

        assertNull(response.adminUserId());
        assertEquals("Admin Team", response.adminUsername());
        assertEquals("Admin Team", response.dmChannelHint());
    }

    @Test
    void shouldSaveLogAndReturnFallback_whenNoUsersExist() {
        when(users.findAll()).thenReturn(List.of());
        when(logs.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EscalateResponse response = service.escalate(userId, "urgent context");

        assertNull(response.adminUserId());
        verify(logs).save(any(EscalationLog.class));
    }
}
