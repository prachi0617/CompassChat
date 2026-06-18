package com.compasschat.ai.service;

import com.compasschat.ai.context.AIContext;
import com.compasschat.ai.context.AIContextBuilder;
import com.compasschat.ai.dto.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AiAgentService {

    private final AIContextBuilder contextBuilder;
    private final GroqProvider groqProvider;
    private final OllamaProvider ollamaProvider;
    private final RuleBasedProvider ruleBasedProvider;

    public AiAgentService(AIContextBuilder contextBuilder,
                          GroqProvider groqProvider,
                          OllamaProvider ollamaProvider,
                          RuleBasedProvider ruleBasedProvider) {
        this.contextBuilder = contextBuilder;
        this.groqProvider = groqProvider;
        this.ollamaProvider = ollamaProvider;
        this.ruleBasedProvider = ruleBasedProvider;
    }

    public ChatResponse processMessage(String message, UUID userId, List<String> history) {
        AIContext context = contextBuilder.build(message, userId);

        // Escalation short-circuits AI call — no need to spend tokens
        if (context.getEscalation().escalate()) {
            return new ChatResponse(
                    ruleBasedProvider.completeFromContext(context),
                    context.getIntent().name(),
                    true
            );
        }

        String responseText = generateResponse(context, message, history);
        return new ChatResponse(responseText, context.getIntent().name(), false);
    }

    private String generateResponse(AIContext context, String message, List<String> history) {
        String systemPrompt = contextBuilder.buildSystemPromptWithHistory(context, history);
        if (groqProvider.isEnabled()) {
            try {
                return groqProvider.complete(systemPrompt, message);
            } catch (Exception ignored) {
                // fall through to Ollama
            }
        }
        if (ollamaProvider.isEnabled()) {
            try {
                return ollamaProvider.complete(systemPrompt, message);
            } catch (Exception ignored) {
                // fall through to rule-based
            }
        }
        return ruleBasedProvider.completeFromContext(context);
    }
}
