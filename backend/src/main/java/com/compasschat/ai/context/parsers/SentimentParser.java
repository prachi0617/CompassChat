package com.compasschat.ai.context.parsers;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SentimentParser {

    // Scores range from -1.0 (severe distress) to +1.0 (very positive)
    private static final Map<String, Double> LEXICON = Map.ofEntries(
            Map.entry("suicidal",    -1.0),
            Map.entry("suicide",     -1.0),
            Map.entry("hopeless",    -0.9),
            Map.entry("desperate",   -0.85),
            Map.entry("crisis",      -0.85),
            Map.entry("starving",    -0.8),
            Map.entry("homeless",    -0.8),
            Map.entry("evicted",     -0.75),
            Map.entry("scared",      -0.7),
            Map.entry("terrified",   -0.7),
            Map.entry("depressed",   -0.6),
            Map.entry("anxious",     -0.5),
            Map.entry("stressed",    -0.45),
            Map.entry("worried",     -0.4),
            Map.entry("sad",         -0.4),
            Map.entry("lonely",      -0.35),
            Map.entry("tired",       -0.25),
            Map.entry("overwhelmed", -0.5),
            Map.entry("frustrated",  -0.35),
            Map.entry("good",         0.4),
            Map.entry("great",        0.6),
            Map.entry("happy",        0.6),
            Map.entry("hopeful",      0.5),
            Map.entry("better",       0.35),
            Map.entry("grateful",     0.55),
            Map.entry("thank",        0.3)
    );

    /**
     * Returns a sentiment score in [-1.0, 1.0].
     * Scores below -0.7 should trigger escalation.
     */
    public double score(String message) {
        String input = message.toLowerCase();
        double total = 0.0;
        int matched = 0;

        for (Map.Entry<String, Double> entry : LEXICON.entrySet()) {
            if (input.contains(entry.getKey())) {
                total += entry.getValue();
                matched++;
            }
        }

        if (matched == 0) return 0.0;
        // Average but keep it within [-1, 1]
        return Math.max(-1.0, Math.min(1.0, total / matched));
    }
}
