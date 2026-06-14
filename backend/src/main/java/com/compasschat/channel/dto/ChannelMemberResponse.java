package com.compasschat.channel.dto;

import com.compasschat.channel.ChannelMember;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChannelMemberResponse(
        UUID userId,
        UUID channelId,
        boolean muted,
        LocalDateTime joinedAt,
        LocalDateTime lastReadAt
) {
    public static ChannelMemberResponse from(ChannelMember m) {
        return new ChannelMemberResponse(
                m.getUserId(),
                m.getChannelId(),
                m.isMuted(),
                m.getJoinedAt(),
                m.getLastReadAt()
        );
    }
}
