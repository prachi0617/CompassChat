package com.compasschat.channel.dto;

import com.compasschat.channel.ChannelMember;
import com.compasschat.channel.MemberRole;
import com.compasschat.channel.NotificationPreference;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChannelMemberResponse(
        UUID userId,
        UUID channelId,
        MemberRole memberRole,
        NotificationPreference notificationPreference,
        boolean muted,
        LocalDateTime joinedAt,
        LocalDateTime lastReadAt
) {
    public static ChannelMemberResponse from(ChannelMember m) {
        return new ChannelMemberResponse(
                m.getUserId(),
                m.getChannelId(),
                m.getMemberRole(),
                m.getNotificationPreference(),
                m.isMuted(),
                m.getJoinedAt(),
                m.getLastReadAt()
        );
    }
}
