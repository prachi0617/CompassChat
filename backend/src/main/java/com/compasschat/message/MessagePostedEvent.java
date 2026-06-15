package com.compasschat.message;

import java.util.UUID;

public class MessagePostedEvent {

    private final UUID messageId;

    public MessagePostedEvent(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getMessageId() {
        return messageId;
    }
}
