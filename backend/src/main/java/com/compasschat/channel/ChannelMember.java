package com.compasschat.channel;

import com.compasschat.common.base.AuditableEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "channel_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_channel_user", columnNames = {"channelId", "userId"})
        },
        indexes = {
                @Index(name = "idx_member_user", columnList = "userId"),
                @Index(name = "idx_member_channel", columnList = "channelId")
        }
)
public class ChannelMember extends AuditableEntity {

    @Column(nullable = false)
    private UUID channelId;

    @Column(nullable = false)
    private UUID userId;

    /** When user last opened this channel - drives unread counts. */
    @Column
    private LocalDateTime lastReadAt;

    @Column(nullable = false)
    private boolean muted = false;

    protected ChannelMember() {} // JPA

    public ChannelMember(UUID channelId, UUID userId) {
        this.channelId = channelId;
        this.userId = userId;
    }

    public void markRead() {
        this.lastReadAt = LocalDateTime.now();
    }

    public void mute()   { this.muted = true; }
    public void unmute() { this.muted = false; }

    public UUID getChannelId() { return channelId; }
    public UUID getUserId() { return userId; }
    public LocalDateTime getLastReadAt() { return lastReadAt; }
    public boolean isMuted() { return muted; }
    /** Convenience alias for createdAt, reads better at call sites. */
    public LocalDateTime getJoinedAt() { return getCreatedAt(); }
}