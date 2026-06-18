package com.compasschat.ai.escalation;

import com.compasschat.common.base.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "ai_escalation_logs")
public class EscalationLog extends AuditableEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(length = 2000)
    private String contextMessage;

    protected EscalationLog() {}

    public EscalationLog(UUID userId, String contextMessage) {
        this.userId = userId;
        this.contextMessage = contextMessage;
    }

    public UUID getUserId() { return userId; }
    public String getContextMessage() { return contextMessage; }
}
