package com.compasschat.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiContextBuilderSimpleTest {

    private AiContextBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new AiContextBuilder();
    }

    @Test
    void shouldReturnNonNullPrompt_whenBuildSystemPromptCalled() {
        String prompt = builder.buildSystemPrompt("I need help with rent");
        assertNotNull(prompt);
        assertFalse(prompt.isBlank());
    }

    @Test
    void shouldIncludeUserMessage_whenBuildSystemPromptCalled() {
        String prompt = builder.buildSystemPrompt("food assistance");
        assertTrue(prompt.contains("food assistance"));
    }

    @Test
    void shouldIncludeCompassChatName_whenBuildSystemPromptCalled() {
        String prompt = builder.buildSystemPrompt("anything");
        assertTrue(prompt.contains("CompassChat"));
    }
}
