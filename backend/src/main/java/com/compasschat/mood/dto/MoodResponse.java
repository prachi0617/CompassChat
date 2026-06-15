package com.compasschat.mood.dto;

import com.compasschat.common.enums.MoodType;
import com.compasschat.mood.Mood;
import com.compasschat.mood.ResourceRecommendationService.RecommendedResource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record MoodResponse(
        UUID id,
        MoodType moodType,
        String note,
        LocalDateTime loggedAt,
        List<MoodResourceResponse> resources
) {
    public static MoodResponse from(Mood mood, List<RecommendedResource> resources) {
        return new MoodResponse(
                mood.getId(),
                mood.getMoodType(),
                mood.getNote(),
                mood.getCreatedAt(),
                resources.stream().map(MoodResourceResponse::from).toList()
        );
    }
}
