package com.compasschat.mood;

import com.compasschat.common.enums.MoodType;
import com.compasschat.mood.ResourceRecommendationService.RecommendedResource;
import com.compasschat.mood.dto.MoodHistoryEntry;
import com.compasschat.mood.dto.MoodResourceResponse;
import com.compasschat.mood.dto.MoodResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MoodDtoTest {

    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldMapMoodToHistoryEntry_whenFromCalled() {
        Mood mood = new Mood(userId, MoodType.HAPPY, "great day");
        MoodHistoryEntry entry = MoodHistoryEntry.from(mood);

        assertEquals(MoodType.HAPPY, entry.moodType());
        assertEquals("great day", entry.note());
        // timeAgo may be "just now" or similar since createdAt is null before persist
    }

    @Test
    void shouldMapMoodAndResourcesToMoodResponse_whenFromCalled() {
        Mood mood = new Mood(userId, MoodType.SAD, "rough day");
        RecommendedResource resource = new RecommendedResource("Title", "Desc", "http://x.com", "rule_based");

        MoodResponse response = MoodResponse.from(mood, List.of(resource));

        assertEquals(MoodType.SAD, response.moodType());
        assertEquals("rough day", response.note());
        assertEquals(1, response.resources().size());
        assertEquals("Title", response.resources().get(0).title());
    }

    @Test
    void shouldMapRecommendedResourceToMoodResourceResponse_whenFromCalled() {
        RecommendedResource rec = new RecommendedResource("Food Bank", "Free food", "http://food.org", "rule_based");
        MoodResourceResponse response = MoodResourceResponse.from(rec);

        assertEquals("Food Bank", response.title());
        assertEquals("Free food", response.summary());
        assertEquals("http://food.org", response.url());
        assertEquals("rule_based", response.source());
    }

    @Test
    void shouldReturnEmptyResourceList_whenFromCalledWithNoResources() {
        Mood mood = new Mood(userId, MoodType.CALM, null);
        MoodResponse response = MoodResponse.from(mood, List.of());

        assertTrue(response.resources().isEmpty());
        assertNull(response.note());
    }
}
