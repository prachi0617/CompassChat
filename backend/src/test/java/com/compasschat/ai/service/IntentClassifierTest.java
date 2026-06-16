package com.compasschat.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IntentClassifierTest {

    private IntentClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new IntentClassifier();
    }

    @Test
    void shouldReturnResourceSearch_whenMessageContainsFood() {
        assertEquals(AiIntent.RESOURCE_SEARCH, classifier.classify("I need food assistance"));
    }

    @Test
    void shouldReturnResourceSearch_whenMessageContainsHousing() {
        assertEquals(AiIntent.RESOURCE_SEARCH, classifier.classify("Looking for housing help"));
    }

    @Test
    void shouldReturnResourceSearch_whenMessageContainsRent() {
        assertEquals(AiIntent.RESOURCE_SEARCH, classifier.classify("Can't pay rent this month"));
    }

    @Test
    void shouldReturnResourceSearch_whenMessageContainsTransportation() {
        assertEquals(AiIntent.RESOURCE_SEARCH, classifier.classify("Need transportation to the clinic"));
    }

    @Test
    void shouldReturnResourceSearch_whenMessageContainsHealthcare() {
        assertEquals(AiIntent.RESOURCE_SEARCH, classifier.classify("Need healthcare coverage"));
    }

    @Test
    void shouldReturnResourceSearch_whenMessageContainsResource() {
        assertEquals(AiIntent.RESOURCE_SEARCH, classifier.classify("what resources are available?"));
    }

    @Test
    void shouldReturnReminder_whenMessageContainsRemind() {
        assertEquals(AiIntent.REMINDER, classifier.classify("Can you remind me about my appointment?"));
    }

    @Test
    void shouldReturnReminder_whenMessageContainsAppointment() {
        assertEquals(AiIntent.REMINDER, classifier.classify("I have an appointment tomorrow"));
    }

    @Test
    void shouldReturnMood_whenMessageContainsSad() {
        assertEquals(AiIntent.MOOD, classifier.classify("I feel really sad today"));
    }

    @Test
    void shouldReturnMood_whenMessageContainsAnxious() {
        assertEquals(AiIntent.MOOD, classifier.classify("I'm feeling anxious"));
    }

    @Test
    void shouldReturnLiveAgent_whenMessageContainsAgent() {
        assertEquals(AiIntent.LIVE_AGENT, classifier.classify("I want to speak to an agent"));
    }

    @Test
    void shouldReturnLiveAgent_whenMessageContainsHuman() {
        assertEquals(AiIntent.LIVE_AGENT, classifier.classify("Can I speak to a human?"));
    }

    @Test
    void shouldReturnUnknown_whenMessageHasNoMatchingKeywords() {
        assertEquals(AiIntent.UNKNOWN, classifier.classify("hello there"));
    }

    @Test
    void shouldBeCaseInsensitive_whenMessageIsUpperCase() {
        assertEquals(AiIntent.RESOURCE_SEARCH, classifier.classify("NEED FOOD HELP"));
    }
}
