package com.compasschat.websocket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record IncomingChatMessage(

        @NotBlank
        @Size(max = 4000)
        String content
) {}
