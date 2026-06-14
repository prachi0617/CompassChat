package com.compasschat.channel.dto;

import com.compasschat.common.enums.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChannelRequest(
        @NotBlank @Size(min = 2, max = 50) String name,
        @Size(max = 1000) String description,
        ChannelType type
) {}
