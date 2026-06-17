package com.compasschat.ai.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;

@Service
public class ResourceSearchService {

    public String search(String query) {

        try {

            ClassPathResource resource =
                    new ClassPathResource(
                            "data/Service_Directory_cleaned.json"
                    );

            String json =
                    Files.readString(
                            resource.getFile().toPath()
                    );

            String lower =
                    query.toLowerCase();

            if (lower.contains("food")) {
                return "Food assistance resources found.";
            }

            if (lower.contains("housing")) {
                return "Housing assistance resources found.";
            }

            if (lower.contains("transportation")) {
                return "Transportation resources found.";
            }

            if (lower.contains("health")) {
                return "Healthcare resources found.";
            }

            return "Community resources available.";

        } catch (IOException ex) {

            return "Unable to load resource directory.";
        }
    }
}