package com.compasschat.ai.integration;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class CivicContextClient {

    public String getRelevantContext(String query) {

        try {

            ClassPathResource resource =
                    new ClassPathResource(
                            "data/news_schema.json"
                    );

            String json =
                    Files.readString(
                            resource.getFile().toPath()
                    );

            String lower =
                    query.toLowerCase();

            if (lower.contains("housing")) {

                return """
                        Housing Update:
                        New housing assistance programs may be available.

                        Why It Matters:
                        Additional support opportunities may reduce wait times.
                        """;
            }

            if (lower.contains("food")) {

                return """
                        Food Assistance Update:

                        Community food distribution events are available.

                        Why It Matters:

                        Families may access supplemental food resources.
                        """;
            }

            return """
                    Civic Update Available.

                    Why It Matters:

                    Community programs and policies may affect available services.
                    """;

        } catch (IOException ex) {

            return """
                    Civic briefing unavailable.
                    """;
        }
    }
}
