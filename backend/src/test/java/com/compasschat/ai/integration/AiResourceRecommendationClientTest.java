package com.compasschat.ai.integration;

import com.compasschat.ai.service.GroqProvider;
import com.compasschat.common.enums.MoodType;
import com.compasschat.mood.ResourceRecommendationService.RecommendedResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiResourceRecommendationClientTest {

    @Mock private GroqProvider groqProvider;

    private AiResourceRecommendationClient client;

    @BeforeEach
    void setUp() {
        client = new AiResourceRecommendationClient(groqProvider);
    }

    @Test
    void shouldReturnDirectoryResources_whenGroqIsDisabled_anxious() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.ANXIOUS, "feeling anxious");

        assertFalse(result.isEmpty());
        verify(groqProvider, never()).complete(any(), any());
    }

    @Test
    void shouldReturnDirectoryResources_whenGroqIsDisabled_stressed() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.STRESSED, "feeling stressed");

        assertNotNull(result);
    }

    @Test
    void shouldReturnGroqRecommendations_whenGroqIsEnabledAndResponds() {
        when(groqProvider.isEnabled()).thenReturn(true);
        when(groqProvider.complete(any(), any()))
                .thenReturn("Delaware 211 | Free community resource line | https://delaware211.org");

        List<RecommendedResource> result = client.recommend(MoodType.SAD, "feeling down");

        assertFalse(result.isEmpty());
        assertEquals("Delaware 211", result.get(0).title());
    }

    @Test
    void shouldFallBackToDirectorySearch_whenGroqThrowsException() {
        when(groqProvider.isEnabled()).thenReturn(true);
        when(groqProvider.complete(any(), any())).thenThrow(new RuntimeException("AI unavailable"));

        List<RecommendedResource> result = client.recommend(MoodType.STRESSED, "overwhelmed");

        assertNotNull(result);
    }

    @Test
    void shouldReturnDirectoryResources_whenMoodIsDistressed() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.DISTRESSED, "can't cope");

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(r ->
                r.source().equals("service_directory") || r.source().equals("groq")));
    }

    @Test
    void shouldReturnResources_whenMoodIsOverwhelmed() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.OVERWHELMED, "too much");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void shouldReturnResources_whenMoodIsTired() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.TIRED, "exhausted");

        assertFalse(result.isEmpty());
    }

    @Test
    void shouldReturnResources_whenMoodIsHappyAndGroqDisabled() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.HAPPY, "feeling great");

        assertNotNull(result);
    }

    @Test
    void shouldReturnResources_whenMoodIsNeutralAndGroqDisabled() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.NEUTRAL, "okay");

        assertNotNull(result);
    }

    @Test
    void shouldHandleMultiplePipedLinesFromGroq_whenGroqReturnsMultipleResources() {
        when(groqProvider.isEnabled()).thenReturn(true);
        when(groqProvider.complete(any(), any())).thenReturn(
                "Resource A | Description A | http://a.com\n" +
                "Resource B | Description B | http://b.com\n" +
                "Resource C | Description C | http://c.com"
        );

        List<RecommendedResource> result = client.recommend(MoodType.LONELY, "alone");

        assertEquals(2, result.size());
    }

    @Test
    void shouldHandleGroqResponseWithMissingFields() {
        when(groqProvider.isEnabled()).thenReturn(true);
        when(groqProvider.complete(any(), any())).thenReturn("Resource A | Description A");

        List<RecommendedResource> result = client.recommend(MoodType.SAD, "sad");

        assertEquals(1, result.size());
        assertEquals("Resource A", result.get(0).title());
        assertEquals("Description A", result.get(0).summary());
        assertEquals("", result.get(0).url());
    }
}
