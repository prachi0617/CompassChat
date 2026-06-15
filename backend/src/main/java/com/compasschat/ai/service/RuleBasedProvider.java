package com.compasschat.ai.service;

import com.compasschat.ai.context.AIContext;
import com.compasschat.ai.context.ContextFragment;
import org.springframework.stereotype.Component;

@Component
public class RuleBasedProvider implements AiProvider {

    @Override
    public String complete(String systemPrompt, String userMessage) {
        // This overload is required by the interface but RuleBasedProvider
        // works better with full AIContext. Call completeFromContext() directly.
        return "I'm here to help. Could you tell me more about what you need?";
    }

    public String completeFromContext(AIContext context) {
        if (context.getEscalation().escalate()) {
            String reason = context.getEscalation().reason();
            if ("high_risk_sentiment".equals(reason)) {
                return "I'm concerned about how you're feeling. You don't have to face this alone — "
                        + "would you like me to connect you with a live support agent right now?";
            }
            return "I'll connect you with a live agent who can assist you directly. "
                    + "Please hold on — someone will be with you shortly.";
        }

        if (context.getFragments().isEmpty()) {
            return "I'm here to help with housing, healthcare, food assistance, youth programs, "
                    + "and more. What are you looking for today?";
        }

        StringBuilder response = new StringBuilder();
        for (ContextFragment fragment : context.getFragments()) {
            response.append(fragment.content()).append("\n\n");
        }
        response.append("Would you like more details or help with something specific?");
        return response.toString().trim();
    }
}
