package com.compasschat.channel.dto;

import jakarta.validation.constraints.Size;

public record UpdateChannelRequest(
        @Size(min = 2, max = 50) String name,
        @Size(max = 1000) String description
) {}
