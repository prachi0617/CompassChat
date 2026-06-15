package com.compasschat.ai.service;

import org.springframework.stereotype.Component;

@Component
public class IntentClassifier {

    public AiIntent classify(String message) {

        String input = message.toLowerCase();

        if (containsAny(input,
                "food",
                "housing",
                "rent",
                "transportation",
                "healthcare",
                "resource")) {

            return AiIntent.RESOURCE_SEARCH;
        }

        if (containsAny(input,
                "remind",
                "appointment",
                "meeting",
                "medicine")) {

            return AiIntent.REMINDER;
        }

        if (containsAny(input,
                "sad",
                "lonely",
                "depressed",
                "anxious",
                "stressed")) {

            return AiIntent.MOOD;
        }

        if (containsAny(input,
                "agent",
                "caseworker",
                "person",
                "human")) {

            return AiIntent.LIVE_AGENT;
        }

        return AiIntent.UNKNOWN;
    }

    private boolean containsAny(String input, String... keywords) {

        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}