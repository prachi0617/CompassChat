package com.compasschat.ai.integration;

import com.compasschat.ai.service.GroqProvider;
import com.compasschat.common.enums.MoodType;
import com.compasschat.mood.ResourceRecommendationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implements ResourceRecommendationService by delegating to Groq when available,
 * falling back to curated keyword-based suggestions when Groq is not configured.
 */
@Service
public class AiResourceRecommendationClient implements ResourceRecommendationService {

    private final GroqProvider groqProvider;

    public AiResourceRecommendationClient(GroqProvider groqProvider) {
        this.groqProvider = groqProvider;
    }

    @Override
    public List<RecommendedResource> recommend(MoodType moodType, String note) {
        if (groqProvider.isEnabled()) {
            try {
                return recommendViaGroq(moodType, note);
            } catch (Exception ignored) {
                // fall through to rule-based
            }
        }
        return ruleBasedRecommendations(moodType);
    }

    private List<RecommendedResource> recommendViaGroq(MoodType moodType, String note) {
        String prompt = String.format(
                "A user logged their mood as %s with the note: \"%s\". " +
                "List 1-2 Delaware community resources that could help them right now. " +
                "Reply as plain text: one resource per line, format: Title | Short description | URL",
                moodType.name(), note == null ? "" : note
        );

        String raw = groqProvider.complete(
                "You are CompassChat, a community resource assistant for Delaware residents. " +
                "Be concise and empathetic.",
                prompt
        );

        return parseGroqResponse(raw);
    }

    private List<RecommendedResource> parseGroqResponse(String raw) {
        return raw.lines()
                .map(String::trim)
                .filter(l -> l.contains("|"))
                .map(l -> {
                    String[] parts = l.split("\\|", 3);
                    return new RecommendedResource(
                            parts[0].trim(),
                            parts.length > 1 ? parts[1].trim() : "",
                            parts.length > 2 ? parts[2].trim() : "",
                            "groq"
                    );
                })
                .limit(2)
                .toList();
    }

    private List<RecommendedResource> ruleBasedRecommendations(MoodType moodType) {
        return switch (moodType) {
            case ANXIOUS, STRESSED, SAD, LONELY -> List.of(
                    new RecommendedResource(
                            "Delaware 211 Helpline",
                            "Free, confidential help connecting you to local health and human services.",
                            "https://www.delaware211.org",
                            "rule_based"
                    )
            );
            case OVERWHELMED, DISTRESSED -> List.of(
                    new RecommendedResource(
                            "NAMI Delaware",
                            "Mental health support, education, and advocacy for Delawareans.",
                            "https://namide.org",
                            "rule_based"
                    )
            );
            case TIRED -> List.of(
                    new RecommendedResource(
                            "Delaware Health & Social Services",
                            "Programs supporting physical and emotional wellbeing across Delaware.",
                            "https://dhss.delaware.gov",
                            "rule_based"
                    )
            );
            default -> List.of();
        };
    }
}
