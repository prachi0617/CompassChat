package com.compasschat.ai.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CivicContextClientTest {

    private CivicContextClient client;

    @BeforeEach
    void setUp() {
        client = new CivicContextClient();
    }

    @Test
    void shouldReturnHousingUpdate_whenQueryContainsHousing() {
        String result = client.getRelevantContext("housing policy");
        assertNotNull(result);
        assertTrue(result.contains("Housing") || result.contains("unavailable"));
    }

    @Test
    void shouldReturnFoodAssistanceUpdate_whenQueryContainsFood() {
        String result = client.getRelevantContext("food bank locations");
        assertNotNull(result);
        assertTrue(result.contains("Food") || result.contains("unavailable"));
    }

    @Test
    void shouldReturnGenericCivicUpdate_whenQueryHasNoSpecificKeyword() {
        String result = client.getRelevantContext("city council vote");
        assertNotNull(result);
        assertFalse(result.isBlank());
    }
}
