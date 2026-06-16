package com.compasschat.ai.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class YouthPathwayClientTest {

    private YouthPathwayClient client;

    @BeforeEach
    void setUp() {
        client = new YouthPathwayClient();
    }

    @Test
    void shouldReturnNonNullString_whenGetContextCalled() {
        String result = client.getContext("job training for youth");
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void shouldReturnYouthOrNotFoundMessage_whenGetContextWithCareerQuery() {
        String result = client.getContext("career education");
        assertNotNull(result);
        assertTrue(result.contains("Youth") || result.contains("No youth") || result.contains("unavailable"));
    }
}
