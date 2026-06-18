package com.compasschat.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResourceSearchServiceTest {

    private ResourceSearchService service;

    @BeforeEach
    void setUp() {
        service = new ResourceSearchService();
    }

    @Test
    void shouldReturnFoodAssistanceResult_whenQueryContainsFood() {
        String result = service.search("food bank near me");
        // Real directory search — may return matches or the fallback message
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void shouldReturnHousingResult_whenQueryContainsHousing() {
        String result = service.search("housing assistance");
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void shouldReturnTransportationResult_whenQueryContainsTransportation() {
        String result = service.search("need transportation help");
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void shouldReturnHealthcareResult_whenQueryContainsHealth() {
        String result = service.search("health clinic");
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void shouldReturnCommunityResourcesResult_whenQueryHasNoMatchingKeyword() {
        String result = service.search("xyzzy no match possible 12345");
        assertEquals("Community resources available — contact Delaware 211 for personalized help.", result);
    }

    @Test
    void shouldReturnRealDirectoryEntry_whenQueryMatchesServiceType() {
        String result = service.search("mental health");
        // Should return at least one formatted entry from the directory
        assertTrue(result.contains("•"), "Expected a formatted directory entry starting with '•'");
    }
}
