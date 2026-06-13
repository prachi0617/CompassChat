package com.compasschat.message;

import com.compasschat.common.base.AuditableEntity;
import com.compasschat.common.enums.AuditAction;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "message_audit_logs",
        indexes = {
                @Index(name = "idx_audit_message", columnList = "messageId"),
                @Index(name = "idx_audit_performed_by", columnList = "performedBy")
        }
)
public class MessageAuditLog extends AuditableEntity {

    @Column(nullable = false)
    private UUID messageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private AuditAction action;

    /** Whoever did the deed - the user id from the JWT. */
    @Column(nullable = false)
    private UUID performedBy;

    @Column(length = 4000)
    private String previousContent;

    @Column(length = 4000)
    private String newContent;

    protected MessageAuditLog() {} // JPA

    public MessageAuditLog(UUID messageId, AuditAction action, UUID performedBy,
                           String previousContent, String newContent) {
        this.messageId = messageId;
        this.action = action;
        this.performedBy = performedBy;
        this.previousContent = previousContent;
        this.newContent = newContent;
    }

    public UUID getMessageId() { return messageId; }
    public AuditAction getAction() { return action; }
    public UUID getPerformedBy() { return performedBy; }
    public String getPreviousContent() { return previousContent; }
    public String getNewContent() { return newContent; }
}

