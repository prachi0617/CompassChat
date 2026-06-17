package com.compasschat.mood;

import com.compasschat.common.enums.MoodType;

import java.util.List;

/**
 * Mood package OWNS this interface. Mood code never
 * imports anything from integration/ - it only knows this contract.
 *
 * Implementation lives at integration/AiResourceRecommendationClient
 * and is annotated @Service so Spring wires it in here.
 */
public interface ResourceRecommendationService {

    /**
     * Look up resources appropriate to the given mood + free-text note.
     * Implementations should check the cache first, then call the AI,
     * then cache the result with a sensible TTL.
     *
     * @param moodType 
     * @param note     
     * @return 
     */
    List<RecommendedResource> recommend(MoodType moodType, String note);

    record RecommendedResource(
            String title,
            String summary,
            String url,
            String source 
    ) {}
}
