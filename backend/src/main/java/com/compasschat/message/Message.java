package com.compasschat.message;

import java.time.LocalDateTime;
import java.util.UUID;

import com.compasschat.common.base.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "messages",
        indexes = {
                @Index(name = "idx_messages_channel_created", columnList = "channelId, createdAt")
        }
)
public class Message extends AuditableEntity {

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false)
    private UUID channelId;

    @Column(nullable = false, length = 4000)
    private String content;

    /** null until the user edits at least once. */
    @Column
    private LocalDateTime editedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    protected Message() {} // JPA

    public Message(UUID senderId, UUID channelId, String content) {
        this.senderId = senderId;
        this.channelId = channelId;
        this.content = content;
    }

    public void editContent(String newContent) {
        this.content = newContent;
        this.editedAt = LocalDateTime.now();
    }

    public void markDeleted() {
        this.deleted = true;
    }

    public UUID getSenderId() { return senderId; }
    public UUID getChannelId() { return channelId; }
    public String getContent() { return content; }
    public LocalDateTime getEditedAt() { return editedAt; }
    public boolean isDeleted() { return deleted; }
}
