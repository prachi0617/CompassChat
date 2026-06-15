package com.compasschat.websocket;

import com.compasschat.common.enums.PresenceStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PresenceUpdate(UUID userId, String username, PresenceStatus status, LocalDateTime changedAt) {}
