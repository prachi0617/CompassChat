package com.compasschat.ai.context;

public record EscalationDecision(boolean escalate, String reason) {

    public static EscalationDecision none() {
        return new EscalationDecision(false, null);
    }

    public static EscalationDecision requestedByUser() {
        return new EscalationDecision(true, "user_requested");
    }

    public static EscalationDecision highRisk() {
        return new EscalationDecision(true, "high_risk_sentiment");
    }
}
