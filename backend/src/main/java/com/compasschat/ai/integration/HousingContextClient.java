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
public class HousingContextClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final List<String> HOUSING_TYPES = List.of(
            "HOUSING", "SHELTER", "EMERGENCY SHELTER", "TRANSITIONAL HOUSING", "RENTAL ASSISTANCE"
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
                if (HOUSING_TYPES.stream().anyMatch(type::contains)) {
                    matches.add(formatService(service));
                    if (matches.size() == 3) break;
                }
            }

            if (matches.isEmpty()) return "No housing resources found in the directory.";

            return "Housing resources available in Delaware:\n" + String.join("\n\n", matches);

        } catch (IOException e) {
            return "Housing resource directory temporarily unavailable.";
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
