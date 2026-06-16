package com.compasschat.ai.service;

import com.compasschat.ai.context.AIContext;
import com.compasschat.ai.context.AIContextBuilder;
import com.compasschat.ai.context.EscalationDecision;
import com.compasschat.ai.context.IntentType;
import com.compasschat.ai.dto.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiAgentServiceTest {

    @Mock private AIContextBuilder contextBuilder;
    @Mock private GroqProvider groqProvider;
    @Mock private RuleBasedProvider ruleBasedProvider;

    private AiAgentService service;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new AiAgentService(contextBuilder, groqProvider, ruleBasedProvider);
    }

    private AIContext contextWithEscalation(boolean escalate) {
        AIContext ctx = new AIContext();
        ctx.setIntent(IntentType.HOUSING);
        ctx.setEscalation(escalate ? EscalationDecision.highRisk() : EscalationDecision.none());
        return ctx;
    }

    @Test
    void shouldReturnEscalatedResponse_whenContextRequiresEscalation() {
        AIContext ctx = contextWithEscalation(true);
        when(contextBuilder.build(anyString(), any())).thenReturn(ctx);
        when(ruleBasedProvider.completeFromContext(ctx)).thenReturn("Please call 988");

        ChatResponse response = service.processMessage("I want to die", userId);

        assertNotNull(response);
        assertTrue(response.liveAgentSuggested());
        assertEquals("Please call 988", response.response());
        verify(groqProvider, never()).complete(anyString(), anyString());
    }

    @Test
    void shouldUseGroq_whenContextIsNotEscalatedAndGroqIsEnabled() {
        AIContext ctx = contextWithEscalation(false);
        when(contextBuilder.build(anyString(), any())).thenReturn(ctx);
        when(contextBuilder.buildSystemPrompt(ctx)).thenReturn("system prompt");
        when(groqProvider.isEnabled()).thenReturn(true);
        when(groqProvider.complete(anyString(), anyString())).thenReturn("AI response");

        ChatResponse response = service.processMessage("I need housing help", userId);

        assertEquals("AI response", response.response());
        assertFalse(response.liveAgentSuggested());
    }

    @Test
    void shouldFallBackToRuleBased_whenGroqThrowsException() {
        AIContext ctx = contextWithEscalation(false);
        when(contextBuilder.build(anyString(), any())).thenReturn(ctx);
        when(contextBuilder.buildSystemPrompt(ctx)).thenReturn("system prompt");
        when(groqProvider.isEnabled()).thenReturn(true);
        when(groqProvider.complete(anyString(), anyString())).thenThrow(new RuntimeException("timeout"));
        when(ruleBasedProvider.completeFromContext(ctx)).thenReturn("Rule-based response");

        ChatResponse response = service.processMessage("hello", userId);

        assertEquals("Rule-based response", response.response());
    }

    @Test
    void shouldUseRuleBased_whenContextIsNotEscalatedAndGroqIsDisabled() {
        AIContext ctx = contextWithEscalation(false);
        when(contextBuilder.build(anyString(), any())).thenReturn(ctx);
        when(groqProvider.isEnabled()).thenReturn(false);
        when(ruleBasedProvider.completeFromContext(ctx)).thenReturn("Rule-based response");

        ChatResponse response = service.processMessage("I need resources", userId);

        assertEquals("Rule-based response", response.response());
        assertFalse(response.liveAgentSuggested());
        verify(groqProvider, never()).complete(anyString(), anyString());
    }
}
