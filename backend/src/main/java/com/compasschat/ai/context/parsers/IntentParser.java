package com.compasschat.ai.context.parsers;

import com.compasschat.ai.context.IntentType;
import org.springframework.stereotype.Component;

@Component
public class IntentParser {

    public IntentType parse(String message) {
        String input = message.toLowerCase();

        if (containsAny(input, "agent", "caseworker", "case worker", "person", "human", "help me now", "talk to someone"))
            return IntentType.ESCALATE;

        if (containsAny(input, "rent", "housing", "voucher", "eviction", "evicted", "shelter", "homeless", "landlord"))
            return IntentType.HOUSING;

        if (containsAny(input, "job", "career", "first step", "resume", "college", "training", "internship", "youth", "future"))
            return IntentType.YOUTH;

        if (containsAny(input, "wilmington", "policy", "news", "civic", "government", "legislation", "vote", "council"))
            return IntentType.CIVIC;

        if (containsAny(input, "food", "transportation", "healthcare", "clinic", "resource", "assistance", "benefit", "snap", "medicaid"))
            return IntentType.RESOURCES;

        if (containsAny(input, "sad", "stressed", "lonely", "anxious", "depressed", "scared", "hopeless", "overwhelmed", "mood", "feeling", "crisis"))
            return IntentType.MOOD;

        if (containsAny(input, "remind", "appointment", "tomorrow", "medicine", "medication", "meeting", "schedule"))
            return IntentType.REMINDER;

        if (containsAny(input, "case", "workflow", "client", "intake", "referral", "follow up", "status"))
            return IntentType.CASEWORKER;

        return IntentType.UNKNOWN;
    }

    private boolean containsAny(String input, String... keywords) {
        for (String kw : keywords) {
            if (input.contains(kw)) return true;
        }
        return false;
    }
}
