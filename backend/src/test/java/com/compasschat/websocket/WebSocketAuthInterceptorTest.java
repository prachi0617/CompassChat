package com.compasschat.websocket;

import com.compasschat.auth.security.JwtService;
import com.compasschat.channel.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketAuthInterceptorTest {

    @Mock private JwtService jwtService;
    @Mock private ChannelService channelService;
    @Mock private MessageChannel channel;

    private WebSocketAuthInterceptor interceptor;

    private final UUID userId = UUID.randomUUID();
    private final UUID channelId = UUID.randomUUID();
    private final String validToken = "valid.jwt.token";

    @BeforeEach
    void setUp() {
        interceptor = new WebSocketAuthInterceptor(jwtService, channelService);
    }

    private Message<byte[]> buildMessage(StompCommand command) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setSessionId("session-1");
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private Message<byte[]> buildMessageWithAuth(StompCommand command, String authHeader) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setSessionId("session-1");
        if (authHeader != null) {
            accessor.addNativeHeader("Authorization", authHeader);
        }
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private Message<byte[]> buildMessageWithDestinationAndUser(StompCommand command, String destination) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        accessor.setSessionId("session-1");
        accessor.setDestination(destination);
        // Set an authenticated user on the accessor
        StompPrincipal principal = new StompPrincipal(userId, "testuser");
        accessor.setUser(new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_MEMBER"))));
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    // --- CONNECT ---

    @Test
    void shouldSetPrincipal_whenConnectWithValidBearerToken() {
        when(jwtService.isValid(validToken)).thenReturn(true);
        when(jwtService.getUserId(validToken)).thenReturn(userId);
        when(jwtService.getUsername(validToken)).thenReturn("alice");
        when(jwtService.getRole(validToken)).thenReturn("MEMBER");

        Message<?> result = interceptor.preSend(
                buildMessageWithAuth(StompCommand.CONNECT, "Bearer " + validToken), channel);

        assertNotNull(result);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenConnectWithMissingAuthorizationHeader() {
        Message<byte[]> msg = buildMessageWithAuth(StompCommand.CONNECT, null);

        assertThrows(IllegalArgumentException.class, () -> interceptor.preSend(msg, channel));
    }

    @Test
    void shouldThrowIllegalArgumentException_whenConnectWithInvalidToken() {
        when(jwtService.isValid(validToken)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> interceptor.preSend(buildMessageWithAuth(StompCommand.CONNECT, "Bearer " + validToken), channel));
    }

    @Test
    void shouldThrowIllegalArgumentException_whenConnectWithMalformedHeader() {
        assertThrows(IllegalArgumentException.class,
                () -> interceptor.preSend(buildMessageWithAuth(StompCommand.CONNECT, "Token abc"), channel));
    }

    // --- SUBSCRIBE ---

    @Test
    void shouldCheckAccess_whenSubscribeToChannelTopic() {
        when(channelService.canUserAccess(channelId, userId)).thenReturn(true);
        String dest = "/topic/channels/" + channelId;

        Message<?> result = interceptor.preSend(
                buildMessageWithDestinationAndUser(StompCommand.SUBSCRIBE, dest), channel);

        assertNotNull(result);
        verify(channelService).canUserAccess(channelId, userId);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenSubscribeToChannelTopicAndAccessDenied() {
        when(channelService.canUserAccess(channelId, userId)).thenReturn(false);
        String dest = "/topic/channels/" + channelId;

        assertThrows(IllegalArgumentException.class,
                () -> interceptor.preSend(
                        buildMessageWithDestinationAndUser(StompCommand.SUBSCRIBE, dest), channel));
    }

    @Test
    void shouldPassThrough_whenSubscribeToNonChannelTopic() {
        Message<byte[]> msg = buildMessageWithDestinationAndUser(StompCommand.SUBSCRIBE, "/topic/presence");

        Message<?> result = interceptor.preSend(msg, channel);

        assertNotNull(result);
        verify(channelService, never()).canUserAccess(any(), any());
    }

    // --- SEND ---

    @Test
    void shouldCheckAccess_whenSendToChannelApp() {
        when(channelService.canUserAccess(channelId, userId)).thenReturn(true);
        String dest = "/app/channels/" + channelId + "/messages";

        Message<?> result = interceptor.preSend(
                buildMessageWithDestinationAndUser(StompCommand.SEND, dest), channel);

        assertNotNull(result);
        verify(channelService).canUserAccess(channelId, userId);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenSendToChannelAppAndAccessDenied() {
        when(channelService.canUserAccess(channelId, userId)).thenReturn(false);
        String dest = "/app/channels/" + channelId + "/messages";

        assertThrows(IllegalArgumentException.class,
                () -> interceptor.preSend(
                        buildMessageWithDestinationAndUser(StompCommand.SEND, dest), channel));
    }

    @Test
    void shouldPassThrough_whenSendToNonChannelPath() {
        Message<byte[]> msg = buildMessageWithDestinationAndUser(StompCommand.SEND, "/app/other");

        Message<?> result = interceptor.preSend(msg, channel);

        assertNotNull(result);
        verify(channelService, never()).canUserAccess(any(), any());
    }

    // --- Default / HEARTBEAT ---

    @Test
    void shouldPassThrough_whenHeartbeatCommand() {
        Message<byte[]> msg = buildMessage(StompCommand.DISCONNECT);

        Message<?> result = interceptor.preSend(msg, channel);

        assertNotNull(result);
        verifyNoInteractions(jwtService, channelService);
    }
}
