package com.compasschat.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMessageRequest(

        @NotBlank(message = "Message content cannot be empty")
        @Size(max = 4000, message = "Message cannot exceed 4000 characters")
        String content
) {}
