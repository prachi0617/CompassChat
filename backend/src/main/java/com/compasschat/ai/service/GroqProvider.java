package com.compasschat.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GroqProvider implements AiProvider {

    private final ChatClient chatClient;
    private final boolean enabled;

    public GroqProvider(ChatClient.Builder builder,
                        @Value("${spring.ai.openai.api-key:}") String apiKey) {
        this.enabled = apiKey != null && !apiKey.isBlank();
        this.chatClient = builder.build();
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String complete(String systemPrompt, String userMessage) {
        if (!enabled) {
            throw new UnsupportedOperationException("Groq API key not configured");
        }
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userMessage)
                .call()
                .content();
    }
}
