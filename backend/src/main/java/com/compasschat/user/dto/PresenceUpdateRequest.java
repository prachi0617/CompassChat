package com.compasschat.user.dto;

import com.compasschat.common.enums.PresenceStatus;
import jakarta.validation.constraints.NotNull;

public record PresenceUpdateRequest(@NotNull PresenceStatus status) {}
