package com.compasschat.ai.dto;

import java.util.List;

public record ChatRequest(
        String message,
        List<String> history
) {
}