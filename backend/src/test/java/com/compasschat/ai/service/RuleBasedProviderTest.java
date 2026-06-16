package com.compasschat.ai.service;

import com.compasschat.ai.context.AIContext;
import com.compasschat.ai.context.ContextFragment;
import com.compasschat.ai.context.ContextSource;
import com.compasschat.ai.context.EscalationDecision;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleBasedProviderTest {

    private final RuleBasedProvider provider = new RuleBasedProvider();

    @Test
    void shouldReturnNonEmptyString_whenCompleteCalledWithPromptAndMessage() {
        String result = provider.complete("system prompt", "user message");

        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void shouldReturnHighRiskResponse_whenContextEscalatedWithHighRiskSentiment() {
        AIContext context = new AIContext();
        context.setEscalation(EscalationDecision.highRisk());

        String result = provider.completeFromContext(context);

        assertTrue(result.contains("concerned") || result.contains("alone"),
                "Expected empathetic crisis response but got: " + result);
    }

    @Test
    void shouldReturnLiveAgentResponse_whenContextEscalatedByUserRequest() {
        AIContext context = new AIContext();
        context.setEscalation(EscalationDecision.requestedByUser());

        String result = provider.completeFromContext(context);

        assertTrue(result.contains("live agent") || result.contains("agent"),
                "Expected live agent response but got: " + result);
    }

    @Test
    void shouldReturnGenericHelpMessage_whenNoEscalationAndNoFragments() {
        AIContext context = new AIContext();

        String result = provider.completeFromContext(context);

        assertTrue(result.contains("help") || result.contains("here"),
                "Expected generic help message but got: " + result);
    }

    @Test
    void shouldIncludeFragmentContent_whenFragmentsPresentAndNoEscalation() {
        AIContext context = new AIContext();
        context.addFragment(new ContextFragment(ContextSource.HOUSING, "Housing resources available in Wilmington."));

        String result = provider.completeFromContext(context);

        assertTrue(result.contains("Housing resources available in Wilmington."),
                "Expected fragment content in response but got: " + result);
    }

    @Test
    void shouldCombineMultipleFragments_whenMultipleFragmentsPresent() {
        AIContext context = new AIContext();
        context.addFragment(new ContextFragment(ContextSource.HOUSING, "Housing info."));
        context.addFragment(new ContextFragment(ContextSource.WELLBEING, "Wellbeing info."));

        String result = provider.completeFromContext(context);

        assertTrue(result.contains("Housing info.") && result.contains("Wellbeing info."));
    }
}
