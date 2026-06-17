package com.compasschat.ai.integration;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class FirstStepClient {

    public String loadResourceData() {

        try {

            var file =
                    new ClassPathResource(
                            "data/Service_Directory_cleaned.json"
                    );

            return Files.readString(
                    file.getFile().toPath()
            );

        } catch (IOException ex) {

            return "Resource data unavailable";
        }
    }
}