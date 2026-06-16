package com.compasschat.ai.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KindConnectClientTest {

    private KindConnectClient client;

    @BeforeEach
    void setUp() {
        client = new KindConnectClient();
    }

    @Test
    void shouldReturnNonNullString_whenGetContextCalled() {
        String result = client.getContext("I feel anxious and sad");
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void shouldReturnWellbeingOrNotFoundMessage_whenGetContextWithMentalHealthQuery() {
        String result = client.getContext("mental health support");
        assertNotNull(result);
        assertTrue(result.contains("wellbeing") || result.contains("No wellbeing") || result.contains("unavailable"));
    }
}
