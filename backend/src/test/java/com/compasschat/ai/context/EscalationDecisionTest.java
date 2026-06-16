package com.compasschat.ai.context;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EscalationDecisionTest {

    @Test
    void shouldReturnNoEscalation_whenNoneFactory() {
        EscalationDecision decision = EscalationDecision.none();

        assertFalse(decision.escalate());
        assertNull(decision.reason());
    }

    @Test
    void shouldReturnEscalationWithUserRequestedReason_whenRequestedByUser() {
        EscalationDecision decision = EscalationDecision.requestedByUser();

        assertTrue(decision.escalate());
        assertEquals("user_requested", decision.reason());
    }

    @Test
    void shouldReturnEscalationWithHighRiskReason_whenHighRisk() {
        EscalationDecision decision = EscalationDecision.highRisk();

        assertTrue(decision.escalate());
        assertEquals("high_risk_sentiment", decision.reason());
    }
}
