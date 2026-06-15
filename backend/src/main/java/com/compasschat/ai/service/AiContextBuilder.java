package com.compasschat.ai.service;

import org.springframework.stereotype.Component;

/**
 * Placeholder — full implementation wired in Phase 2.
 * Builds a structured AIContext from user input for downstream AI providers.
 */
@Component
public class AiContextBuilder {

    public AiContextBuilder() {}

    public String buildSystemPrompt(String userMessage) {
        return "You are CompassChat, a community resource assistant. Help the user with: "
                + userMessage;
    }
}
