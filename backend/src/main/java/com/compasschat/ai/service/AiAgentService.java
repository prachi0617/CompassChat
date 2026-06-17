package com.compasschat.ai.service;

import com.compasschat.ai.context.AIContext;
import com.compasschat.ai.context.AIContextBuilder;
import com.compasschat.ai.dto.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AiAgentService {

    private final AIContextBuilder contextBuilder;
    private final GroqProvider groqProvider;
    private final RuleBasedProvider ruleBasedProvider;

    public AiAgentService(AIContextBuilder contextBuilder,
                          GroqProvider groqProvider,
                          RuleBasedProvider ruleBasedProvider) {
        this.contextBuilder = contextBuilder;
        this.groqProvider = groqProvider;
        this.ruleBasedProvider = ruleBasedProvider;
    }

    public ChatResponse processMessage(String message, UUID userId) {
        AIContext context = contextBuilder.build(message, userId);

        // Escalation short-circuits AI call — no need to spend tokens
        if (context.getEscalation().escalate()) {
            return new ChatResponse(
                    ruleBasedProvider.completeFromContext(context),
                    context.getIntent().name(),
                    true
            );
        }

        String responseText = generateResponse(context, message);
        return new ChatResponse(responseText, context.getIntent().name(), false);
    }

    private String generateResponse(AIContext context, String message) {
        if (groqProvider.isEnabled()) {
            try {
                String systemPrompt = contextBuilder.buildSystemPrompt(context);
                return groqProvider.complete(systemPrompt, message);
            } catch (Exception e) {
                // Fall through to rule-based on any Groq failure
            }
        }
        return ruleBasedProvider.completeFromContext(context);
    }
}
