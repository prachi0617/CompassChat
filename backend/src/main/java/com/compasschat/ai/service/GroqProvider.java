package com.compasschat.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GroqProvider implements AiProvider {

    private final ChatClient chatClient;
    private final boolean enabled;

    /**
     * ChatClient.Builder is optional so Spring does not force the OpenAiChatModel
     * bean (and its mandatory-key check) when GROQ_API_KEY is absent.
     */
    public GroqProvider(@Autowired(required = false) ChatClient.Builder builder,
                        @Value("${spring.ai.openai.api-key:}") String apiKey) {
        boolean keyPresent = apiKey != null && !apiKey.isBlank();
        this.enabled = keyPresent && builder != null;
        this.chatClient = this.enabled ? builder.build() : null;
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
