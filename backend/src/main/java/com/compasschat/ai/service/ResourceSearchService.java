package com.compasschat.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ResourceSearchService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Set<String> STOP_WORDS = Set.of(
            "about", "also", "assistance", "available", "been", "come", "does",
            "find", "from", "have", "help", "here", "info", "information",
            "into", "just", "know", "like", "more", "much", "need", "offer",
            "offers", "only", "other", "please", "some", "support", "tell", "than",
            "that", "their", "them", "there", "they", "this", "want", "what",
            "when", "where", "which", "with", "would", "your"
    );

    public String search(String query) {
        try {
            InputStream stream = new ClassPathResource("data/Service_Directory_cleaned.json")
                    .getInputStream();
            JsonNode root = MAPPER.readTree(stream);
            JsonNode services = root.has("services") ? root.path("services") : root;

            String lower = query.toLowerCase();
            String[] words = lower.split("\\s+");
            List<String> typeMatches = new ArrayList<>();
            List<String> descMatches = new ArrayList<>();
            Set<String> seen = new java.util.LinkedHashSet<>();

            for (JsonNode s : services) {
                String orgName = s.path("organizationName").asText();
                if (seen.contains(orgName)) continue;
                String type = s.path("typeOfService").asText("").toLowerCase();
                String desc = s.path("servicesDescription").asText("").toLowerCase();
                for (String word : words) {
                    if (word.length() > 3 && !STOP_WORDS.contains(word)) {
                        if (type.contains(word) && typeMatches.size() < 3) {
                            typeMatches.add(format(s));
                            seen.add(orgName);
                            break;
                        } else if (desc.contains(word) && descMatches.size() < 3) {
                            descMatches.add(format(s));
                            seen.add(orgName);
                            break;
                        }
                    }
                }
                if (typeMatches.size() == 3) break;
            }

            List<String> results = new ArrayList<>(typeMatches);
            for (String d : descMatches) {
                if (results.size() >= 3) break;
                results.add(d);
            }

            return results.isEmpty()
                    ? "Community resources available — contact Delaware 211 for personalized help."
                    : String.join("\n\n", results);

        } catch (IOException e) {
            return "Unable to load resource directory.";
        }
    }

    private String format(JsonNode s) {
        return String.format("• %s\n  %s\n  Phone: %s | %s",
                s.path("organizationName").asText(),
                s.path("servicesDescription").asText(),
                s.path("phone").asText("N/A"),
                s.path("website").asText(s.path("fullAddress").asText("")));
    }
}