package com.compasschat.websocket;

import com.compasschat.common.enums.PresenceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresenceEventListenerTest {

    @Mock private PresenceService presenceService;

    private PresenceEventListener listener;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        listener = new PresenceEventListener(presenceService);
    }

    private Message<byte[]> stompMessage() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        accessor.setSessionId("s1");
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private UsernamePasswordAuthenticationToken authToken() {
        StompPrincipal principal = new StompPrincipal(userId, "alice");
        return new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
    }

    @Test
    void shouldSetOnline_whenConnectedWithValidPrincipal() {
        SessionConnectedEvent event = new SessionConnectedEvent(this, stompMessage(), authToken());

        listener.onConnected(event);

        verify(presenceService).setStatus(eq(userId), eq(PresenceStatus.ONLINE));
    }

    @Test
    void shouldSkip_whenConnectedWithNullPrincipal() {
        SessionConnectedEvent event = new SessionConnectedEvent(this, stompMessage(), (Principal) null);

        listener.onConnected(event);

        verifyNoInteractions(presenceService);
    }

    @Test
    void shouldSkip_whenConnectedWithNonStompPrincipal() {
        Principal plainPrincipal = () -> "plain-user";
        SessionConnectedEvent event = new SessionConnectedEvent(this, stompMessage(), plainPrincipal);

        listener.onConnected(event);

        verifyNoInteractions(presenceService);
    }

    @Test
    void shouldSetOffline_whenDisconnectedWithValidPrincipal() {
        SessionDisconnectEvent event = new SessionDisconnectEvent(this, stompMessage(), "s1", null, authToken());

        listener.onDisconnected(event);

        verify(presenceService).setStatus(eq(userId), eq(PresenceStatus.OFFLINE));
    }

    @Test
    void shouldSkip_whenDisconnectedWithNullPrincipal() {
        SessionDisconnectEvent event = new SessionDisconnectEvent(this, stompMessage(), "s1", null, null);

        listener.onDisconnected(event);

        verifyNoInteractions(presenceService);
    }
}
