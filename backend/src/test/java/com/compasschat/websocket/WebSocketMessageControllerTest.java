package com.compasschat.websocket;

import com.compasschat.message.Message;
import com.compasschat.message.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketMessageControllerTest {

    @Mock private MessageService messageService;

    private WebSocketMessageController controller;

    private final UUID channelId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        controller = new WebSocketMessageController(messageService);
    }

    private UsernamePasswordAuthenticationToken authToken() {
        StompPrincipal principal = new StompPrincipal(userId, "alice");
        return new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
    }

    @Test
    void shouldPostMessage_whenSendMessageWithValidPrincipal() {
        Message msg = new Message(userId, channelId, "hello");
        when(messageService.post(eq(channelId), eq(userId), eq("hello"))).thenReturn(msg);

        controller.sendMessage(channelId,
                new com.compasschat.websocket.dto.IncomingChatMessage("hello"),
                authToken());

        verify(messageService).post(channelId, userId, "hello");
    }

    @Test
    void shouldThrowAccessDeniedException_whenSendMessageWithNullPrincipal() {
        assertThrows(AccessDeniedException.class, () ->
                controller.sendMessage(channelId,
                        new com.compasschat.websocket.dto.IncomingChatMessage("hi"),
                        null));
    }

    @Test
    void shouldReturnTypingIndicator_whenTypingWithValidPrincipal() {
        var incoming = new com.compasschat.websocket.dto.TypingIndicator(channelId, UUID.randomUUID(), "other", true);

        var result = controller.typing(channelId, incoming, authToken());

        assertNotNull(result);
        assertEquals(channelId, result.channelId());
        assertEquals(userId, result.userId());
        assertEquals("alice", result.username());
        assertTrue(result.typing());
    }

    @Test
    void shouldReturnErrorMessage_whenHandleErrorCalled() {
        String msg = controller.handleError(new RuntimeException("something broke"));
        assertEquals("something broke", msg);
    }
}
