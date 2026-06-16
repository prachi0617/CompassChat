package com.compasschat.ai.context.parsers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SentimentParserTest {

    private final SentimentParser parser = new SentimentParser();

    @Test
    void shouldReturnZero_whenNoKeywordsPresent() {
        assertEquals(0.0, parser.score(""), 0.001);
        assertEquals(0.0, parser.score("hello there how are you"), 0.001);
    }

    @Test
    void shouldReturnHighlyNegativeScore_whenHighRiskWordPresent() {
        double score = parser.score("I feel suicidal");
        assertTrue(score <= -0.7, "Expected score ≤ -0.7 but was " + score);
    }

    @Test
    void shouldReturnPositiveScore_whenPositiveWordPresent() {
        double score = parser.score("I am feeling happy today");
        assertTrue(score > 0.0, "Expected positive score but was " + score);
    }

    @Test
    void shouldReturnScoreAtOrBelowEscalationThreshold_whenScaredKeywordUsed() {
        // "scared" has lexicon score -0.7; single match returns exactly -0.7
        double score = parser.score("I am so scared");
        assertTrue(score <= -0.7, "Expected score ≤ -0.7 for 'scared' but was " + score);
    }

    @Test
    void shouldClampToNegativeOne_whenAverageExceedsLowerBound() {
        // "suicidal" (-1.0) + "suicide" (-1.0) average = -1.0, clamp keeps it at -1.0
        double score = parser.score("I feel suicidal and thinking about suicide");
        assertEquals(-1.0, score, 0.001);
    }

    @Test
    void shouldAverageMultipleKeywords_whenMixedSentimentPresent() {
        // "hopeless" (-0.9) + "hopeful" (+0.5) → average = -0.2
        double score = parser.score("I feel hopeless but also hopeful");
        assertTrue(score > -1.0 && score < 0.0,
                "Expected averaged negative score but was " + score);
    }

    @Test
    void shouldReturnPositiveScore_whenOnlyGratefulKeywordPresent() {
        double score = parser.score("I am so grateful for your help");
        assertTrue(score > 0.0);
    }
}
