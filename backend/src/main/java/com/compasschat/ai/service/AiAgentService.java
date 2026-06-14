package com.compasschat.ai.service;

import com.compasschat.ai.dto.ChatResponse;
import com.compasschat.ai.integration.FirstStepClient;

import org.springframework.stereotype.Service;

@Service
public class AiAgentService {

    private final IntentClassifier classifier;

    private final FirstStepClient firstStepClient;

    public AiAgentService(
            IntentClassifier classifier,
            FirstStepClient firstStepClient) {

        this.classifier = classifier;
        this.firstStepClient = firstStepClient;
    }

    public ChatResponse processMessage(String message) {

        AiIntent intent =
                classifier.classify(message);

        return switch (intent) {

            case RESOURCE_SEARCH ->
                    new ChatResponse(
                            "I found community resources that may help.",
                            intent.name(),
                            false
                    );

            case REMINDER ->
                    new ChatResponse(
                            "I can help create a reminder.",
                            intent.name(),
                            false
                    );

            case MOOD ->
                    new ChatResponse(
                            "Thank you for sharing how you feel. Would you like to talk with a live agent?",
                            intent.name(),
                            true
                    );

            case LIVE_AGENT ->
                    new ChatResponse(
                            "Connecting you to a live agent.",
                            intent.name(),
                            true
                    );

            default ->
                    new ChatResponse(
                            "Can you tell me more about what you need?",
                            intent.name(),
                            false
                    );
        };
    }
}
