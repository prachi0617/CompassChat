package com.compasschat.message;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MessageDomainTest {

    private final UUID senderId = UUID.randomUUID();
    private final UUID channelId = UUID.randomUUID();

    @Test
    void shouldInitialiseFields_whenConstructed() {
        Message msg = new Message(senderId, channelId, "Hello");

        assertEquals(senderId, msg.getSenderId());
        assertEquals(channelId, msg.getChannelId());
        assertEquals("Hello", msg.getContent());
        assertNull(msg.getEditedAt());
        assertFalse(msg.isDeleted());
    }

    @Test
    void shouldUpdateContentAndSetEditedAt_whenEditContentCalled() {
        Message msg = new Message(senderId, channelId, "original");

        msg.editContent("updated");

        assertEquals("updated", msg.getContent());
        assertNotNull(msg.getEditedAt());
    }

    @Test
    void shouldSetDeletedFlagToTrue_whenMarkDeletedCalled() {
        Message msg = new Message(senderId, channelId, "bye");

        assertFalse(msg.isDeleted());
        msg.markDeleted();
        assertTrue(msg.isDeleted());
    }

    @Test
    void shouldUpdateEditedAtAgain_whenEditContentCalledTwice() {
        Message msg = new Message(senderId, channelId, "v1");
        msg.editContent("v2");
        var firstEditTime = msg.getEditedAt();

        msg.editContent("v3");

        assertEquals("v3", msg.getContent());
        assertNotNull(msg.getEditedAt());
        // editedAt is refreshed on each edit
        assertFalse(msg.getEditedAt().isBefore(firstEditTime));
    }
}
