package com.compasschat.dm.dto;

import com.compasschat.dm.DirectMessage;

import java.time.LocalDateTime;
import java.util.UUID;

public record DirectMessageResponse(
        UUID id,
        UUID senderId,
        UUID recipientId,
        String content,
        LocalDateTime editedAt,
        LocalDateTime createdAt
) {
    public static DirectMessageResponse from(DirectMessage dm) {
        return new DirectMessageResponse(
                dm.getId(),
                dm.getSenderId(),
                dm.getRecipientId(),
                dm.getContent(),
                dm.getEditedAt(),
                dm.getCreatedAt()
        );
    }
}
