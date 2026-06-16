package com.compasschat.dm;

import com.compasschat.common.base.AuditableEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "direct_messages",
        indexes = {
                @Index(name = "idx_dm_sender_recipient", columnList = "senderId, recipientId, createdAt"),
                @Index(name = "idx_dm_recipient_sender", columnList = "recipientId, senderId, createdAt")
        }
)
public class DirectMessage extends AuditableEntity {

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false)
    private UUID recipientId;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column
    private LocalDateTime editedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    protected DirectMessage() {}

    public DirectMessage(UUID senderId, UUID recipientId, String content) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
    }

    public void editContent(String newContent) {
        this.content = newContent;
        this.editedAt = LocalDateTime.now();
    }

    public void markDeleted() {
        this.deleted = true;
    }

    public UUID getSenderId()    { return senderId; }
    public UUID getRecipientId() { return recipientId; }
    public String getContent()   { return content; }
    public LocalDateTime getEditedAt() { return editedAt; }
    public boolean isDeleted()   { return deleted; }
}
