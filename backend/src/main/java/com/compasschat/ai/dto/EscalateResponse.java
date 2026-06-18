package com.compasschat.ai.dto;

import java.util.UUID;

public record EscalateResponse(UUID adminUserId, String adminUsername, String dmChannelHint) {}
