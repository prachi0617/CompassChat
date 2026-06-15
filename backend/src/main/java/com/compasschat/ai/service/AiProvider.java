package com.compasschat.ai.service;

public interface AiProvider {
    String complete(String systemPrompt, String userMessage);
}
