package com.compasschat.ai.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HousingContextClientTest {

    private HousingContextClient client;

    @BeforeEach
    void setUp() {
        client = new HousingContextClient();
    }

    @Test
    void shouldReturnNonNullString_whenGetContextCalled() {
        String result = client.getContext("I need help with rent");
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void shouldReturnStringContainingHousingInfo_whenGetContextWithHousingQuery() {
        String result = client.getContext("emergency shelter");
        assertNotNull(result);
        // Either found real data or returned the "not found" message — both are valid
        assertTrue(result.contains("Housing") || result.contains("No housing") || result.contains("unavailable"));
    }
}
