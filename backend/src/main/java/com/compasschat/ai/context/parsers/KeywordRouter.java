package com.compasschat.ai.context.parsers;

import com.compasschat.ai.context.ContextFragment;
import com.compasschat.ai.context.ContextSource;
import org.springframework.stereotype.Component;

@Component
public class KeywordRouter {

    /**
     * Last-resort router: produces a generic resource fragment when
     * IntentParser returns UNKNOWN. Always returns a non-null fragment
     * so AIContextBuilder always has something to send the provider.
     */
    public ContextFragment route(String message) {
        String input = message.toLowerCase();

        if (containsAny(input, "mental health", "counseling", "therapy", "wellbeing", "well-being"))
            return new ContextFragment(ContextSource.WELLBEING,
                    "Mental health and counseling services are available in the community.");

        if (containsAny(input, "child", "family", "parent", "youth", "teen", "kid"))
            return new ContextFragment(ContextSource.YOUTH,
                    "Youth and family support programs are available.");

        if (containsAny(input, "legal", "rights", "court", "attorney", "advocate"))
            return new ContextFragment(ContextSource.CIVIC,
                    "Legal aid and advocacy resources are available in Delaware.");

        return new ContextFragment(ContextSource.RESOURCE_DIRECTORY,
                "General community resources are available. Ask about housing, food, healthcare, or youth services.");
    }

    private boolean containsAny(String input, String... keywords) {
        for (String kw : keywords) {
            if (input.contains(kw)) return true;
        }
        return false;
    }
}
