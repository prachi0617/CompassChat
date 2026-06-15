package com.compasschat.websocket.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record OutgoingChatMessage(
        UUID messageId,
        UUID channelId,
        UUID senderId,
        String content,
        LocalDateTime sentAt
) {}
