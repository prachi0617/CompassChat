package com.compasschat.ai.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class YouthPathwayClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final List<String> YOUTH_TYPES = List.of(
            "YOUTH", "EDUCATION", "TRAINING", "EMPLOYMENT", "JOB", "CAREER",
            "AFTER SCHOOL", "AFTERSCHOOL", "COLLEGE", "VOCATIONAL"
    );

    public String getContext(String query) {
        try {
            InputStream stream = new ClassPathResource("data/Service_Directory_cleaned.json")
                    .getInputStream();
            JsonNode root = MAPPER.readTree(stream);
            JsonNode services = root.path("services");

            List<String> matches = new ArrayList<>();
            for (JsonNode service : services) {
                String type = service.path("typeOfService").asText("").toUpperCase();
                if (YOUTH_TYPES.stream().anyMatch(type::contains)) {
                    matches.add(formatService(service));
                    if (matches.size() == 9) break;
                }
            }

            if (matches.isEmpty()) return "No youth pathway resources found in the directory.";

            return "Youth and career pathway resources:\n" + String.join("\n\n", matches);

        } catch (IOException e) {
            return "Youth resource directory temporarily unavailable.";
        }
    }

    private String formatService(JsonNode s) {
        return String.format("• %s\n  %s\n  Phone: %s | %s",
                s.path("organizationName").asText(),
                s.path("servicesDescription").asText(),
                s.path("phone").asText("N/A"),
                s.path("fullAddress").asText(""));
    }
}
