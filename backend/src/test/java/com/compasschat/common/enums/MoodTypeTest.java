package com.compasschat.common.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class MoodTypeTest {

    @Test
    void shouldReturnTrue_whenMoodIsOverwhelmed() {
        assertTrue(MoodType.OVERWHELMED.isDistressed());
    }

    @Test
    void shouldReturnTrue_whenMoodIsDistressed() {
        assertTrue(MoodType.DISTRESSED.isDistressed());
    }

    @ParameterizedTest
    @EnumSource(value = MoodType.class, names = {"HAPPY", "HOPEFUL", "CALM", "GRATEFUL",
            "NEUTRAL", "TIRED", "SAD", "LONELY", "ANXIOUS", "ANGRY", "STRESSED"})
    void shouldReturnFalse_whenMoodIsNotDistressed(MoodType mood) {
        assertFalse(mood.isDistressed());
    }
}
