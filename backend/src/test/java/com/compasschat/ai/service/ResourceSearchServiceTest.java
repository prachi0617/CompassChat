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
        assertEquals("Food assistance resources found.", result);
    }

    @Test
    void shouldReturnHousingResult_whenQueryContainsHousing() {
        String result = service.search("housing assistance");
        assertEquals("Housing assistance resources found.", result);
    }

    @Test
    void shouldReturnTransportationResult_whenQueryContainsTransportation() {
        String result = service.search("need transportation help");
        assertEquals("Transportation resources found.", result);
    }

    @Test
    void shouldReturnHealthcareResult_whenQueryContainsHealth() {
        String result = service.search("health clinic");
        assertEquals("Healthcare resources found.", result);
    }

    @Test
    void shouldReturnCommunityResourcesResult_whenQueryHasNoMatchingKeyword() {
        String result = service.search("general assistance");
        assertEquals("Community resources available.", result);
    }
}
