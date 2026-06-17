package com.compasschat.mood.dto;

import com.compasschat.mood.ResourceRecommendationService.RecommendedResource;

public record MoodResourceResponse(
        String title,
        String summary,
        String url,
        String source
) {
    public static MoodResourceResponse from(RecommendedResource r) {
        return new MoodResourceResponse(r.title(), r.summary(), r.url(), r.source());
    }
}