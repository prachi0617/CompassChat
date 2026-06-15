package com.compasschat.message.dto;

import com.compasschat.common.enums.AuditAction;
import com.compasschat.message.MessageAuditLog;

import java.time.LocalDateTime;
import java.util.UUID;

/** What moderator tools and history views get back. */
public record MessageAuditLogResponse(
        UUID id,
        UUID messageId,
        AuditAction action,
        UUID performedBy,
        String previousContent,
        String newContent,
        LocalDateTime occurredAt
) {
    public static MessageAuditLogResponse from(MessageAuditLog log) {
        return new MessageAuditLogResponse(
                log.getId(),
                log.getMessageId(),
                log.getAction(),
                log.getPerformedBy(),
                log.getPreviousContent(),
                log.getNewContent(),
                log.getCreatedAt()
        );
    }
}
