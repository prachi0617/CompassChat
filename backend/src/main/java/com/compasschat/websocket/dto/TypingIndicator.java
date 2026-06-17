package com.compasschat.websocket.dto;

import java.util.UUID;

public record TypingIndicator(
        UUID channelId,
        UUID userId,
        String username,
        boolean typing
) {}