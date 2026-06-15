package com.compasschat.message.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.compasschat.message.Message;

public record MessageResponse(
        UUID id,
        UUID senderId,
        UUID channelId,
        String content,
        boolean edited,
        LocalDateTime editedAt,
        LocalDateTime createdAt
) {
    public static MessageResponse from(Message m) {
        return new MessageResponse(
                m.getId(),
                m.getSenderId(),
                m.getChannelId(),
                m.getContent(),
                m.getEditedAt() != null,
                m.getEditedAt(),
                m.getCreatedAt()
        );
    }
}
