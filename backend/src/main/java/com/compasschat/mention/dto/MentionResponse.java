package com.compasschat.mention.dto;

import com.compasschat.mention.Mention;

import java.time.LocalDateTime;
import java.util.UUID;

public record MentionResponse(
        UUID id,
        UUID messageId,
        UUID channelId,
        UUID senderUserId,
        UUID mentionedUserId,
        boolean read,
        LocalDateTime createdAt
) {
    public static MentionResponse from(Mention m) {
        return new MentionResponse(
                m.getId(),
                m.getMessageId(),
                m.getChannelId(),
                m.getSenderUserId(),
                m.getMentionedUserId(),
                m.isRead(),
                m.getCreatedAt()
        );
    }
}
