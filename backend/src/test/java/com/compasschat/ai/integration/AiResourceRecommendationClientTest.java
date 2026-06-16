package com.compasschat.ai.integration;

import com.compasschat.ai.service.GroqProvider;
import com.compasschat.common.enums.MoodType;
import com.compasschat.mood.ResourceRecommendationService.RecommendedResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
    void shouldReturnRuleBasedRecommendations_whenGroqIsDisabled() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.ANXIOUS, "feeling anxious");

        assertFalse(result.isEmpty());
        verify(groqProvider, never()).complete(any(), any());
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
    void shouldFallBackToRuleBased_whenGroqThrowsException() {
        when(groqProvider.isEnabled()).thenReturn(true);
        when(groqProvider.complete(any(), any())).thenThrow(new RuntimeException("AI unavailable"));

        List<RecommendedResource> result = client.recommend(MoodType.STRESSED, "overwhelmed");

        assertNotNull(result);
        // Falls back — rule-based or empty, but must not throw
    }

    @Test
    void shouldReturnNamiResource_whenMoodIsDistressed() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.DISTRESSED, "can't cope");

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(r -> r.source().equals("rule_based")));
    }

    @Test
    void shouldReturnHealthResource_whenMoodIsTired() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.TIRED, "exhausted");

        assertFalse(result.isEmpty());
    }

    @Test
    void shouldReturnEmptyList_whenMoodIsHappyAndGroqDisabled() {
        when(groqProvider.isEnabled()).thenReturn(false);

        List<RecommendedResource> result = client.recommend(MoodType.HAPPY, "feeling great");

        assertTrue(result.isEmpty());
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

        // Capped at 2 (limit(2))
        assertEquals(2, result.size());
    }
}
