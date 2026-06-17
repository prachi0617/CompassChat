package com.compasschat.mood.dto;

import com.compasschat.common.enums.MoodType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateMoodRequest(

        @NotNull(message = "Mood is required")
        MoodType moodType,

        @Size(max = 1000, message = "Note must be 1000 characters or fewer")
        String note
) {}
