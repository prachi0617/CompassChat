package com.compasschat.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceSearchService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String search(String query) {
        try {
            InputStream stream = new ClassPathResource("data/Service_Directory_cleaned.json")
                    .getInputStream();
            JsonNode root = MAPPER.readTree(stream);
            JsonNode services = root.has("services") ? root.path("services") : root;

            String lower = query.toLowerCase();
            List<String> matches = new ArrayList<>();

            for (JsonNode s : services) {
                String type = s.path("typeOfService").asText("").toLowerCase();
                String desc = s.path("servicesDescription").asText("").toLowerCase();
                if (type.contains(lower) || desc.contains(lower)) {
                    matches.add(format(s));
                    if (matches.size() == 3) break;
                }
            }

            return matches.isEmpty()
                    ? "Community resources available — contact Delaware 211 for personalized help."
                    : String.join("\n\n", matches);

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