package com.compasschat.ai.dto;

public record ChatResponse(
        String response,
        String intent,
        boolean liveAgentSuggested
) {
}
