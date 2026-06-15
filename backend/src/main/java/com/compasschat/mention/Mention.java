package com.compasschat.mention;

import com.compasschat.common.base.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(
        name = "mentions",
        indexes = {
                @Index(name = "idx_mention_mentioned_user", columnList = "mentionedUserId"),
                @Index(name = "idx_mention_message",        columnList = "messageId")
        }
)
public class Mention extends AuditableEntity {

    @Column(nullable = false)
    private UUID messageId;

    @Column(nullable = false)
    private UUID channelId;

    /** The user who typed the @mention. */
    @Column(nullable = false)
    private UUID senderUserId;

    /** The user who was @mentioned. */
    @Column(nullable = false)
    private UUID mentionedUserId;

    @Column(nullable = false)
    private boolean read = false;

    protected Mention() {}

    public Mention(UUID messageId, UUID channelId, UUID senderUserId, UUID mentionedUserId) {
        this.messageId = messageId;
        this.channelId = channelId;
        this.senderUserId = senderUserId;
        this.mentionedUserId = mentionedUserId;
    }

    public void markRead() { this.read = true; }

    public UUID getMessageId()      { return messageId; }
    public UUID getChannelId()      { return channelId; }
    public UUID getSenderUserId()   { return senderUserId; }
    public UUID getMentionedUserId(){ return mentionedUserId; }
    public boolean isRead()         { return read; }
}
