package com.compasschat.websocket;

import com.compasschat.common.enums.PresenceStatus;
import com.compasschat.websocket.dto.IncomingChatMessage;
import com.compasschat.websocket.dto.OutgoingChatMessage;
import com.compasschat.websocket.dto.PresenceUpdate;
import com.compasschat.websocket.dto.TypingIndicator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketDtoTest {

    @Test
    void shouldCreateIncomingChatMessage_whenConstructed() {
        IncomingChatMessage msg = new IncomingChatMessage("hello");
        assertEquals("hello", msg.content());
    }

    @Test
    void shouldCreateOutgoingChatMessage_whenConstructed() {
        UUID msgId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        OutgoingChatMessage msg = new OutgoingChatMessage(msgId, channelId, senderId, "hi", now);

        assertEquals(msgId, msg.messageId());
        assertEquals(channelId, msg.channelId());
        assertEquals("hi", msg.content());
        assertEquals(now, msg.sentAt());
    }

    @Test
    void shouldCreateTypingIndicator_whenConstructed() {
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        TypingIndicator indicator = new TypingIndicator(channelId, userId, "alice", true);

        assertEquals(channelId, indicator.channelId());
        assertEquals("alice", indicator.username());
        assertTrue(indicator.typing());
    }

    @Test
    void shouldCreatePresenceUpdate_whenConstructed() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        PresenceUpdate update = new PresenceUpdate(userId, "bob", PresenceStatus.ONLINE, now);

        assertEquals(userId, update.userId());
        assertEquals("bob", update.username());
        assertEquals(PresenceStatus.ONLINE, update.status());
    }
}
