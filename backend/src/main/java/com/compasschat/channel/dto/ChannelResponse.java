package com.compasschat.channel.dto;

import com.compasschat.channel.Channel;
import com.compasschat.common.enums.ChannelType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChannelResponse(
        UUID id,
        String name,
        String description,
        String purpose,
        ChannelType type,
        String linkedSubProject,
        boolean archived,
        long memberCount,
        LocalDateTime createdAt
) {
    public static ChannelResponse from(Channel ch, long memberCount) {
        return new ChannelResponse(
                ch.getId(),
                ch.getName(),
                ch.getDescription(),
                ch.getPurpose(),
                ch.getType(),
                ch.getLinkedSubProject(),
                ch.isArchived(),
                memberCount,
                ch.getCreatedAt()
        );
    }
}
