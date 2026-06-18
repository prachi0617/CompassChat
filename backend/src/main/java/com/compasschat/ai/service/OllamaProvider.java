package com.compasschat.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OllamaProvider implements AiProvider {

    private final RestClient restClient;
    private final String model;
    private final boolean enabled;

    public OllamaProvider(
            @Value("${ollama.api.url:}") String apiUrl,
            @Value("${ollama.model:gemma2:2b}") String model) {
        this.model = model;
        this.enabled = apiUrl != null && !apiUrl.isBlank();
        this.restClient = this.enabled ? RestClient.builder().baseUrl(apiUrl).build() : null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String complete(String systemPrompt, String userMessage) {
        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", systemPrompt + "\n\nUser: " + userMessage,
                "stream", false
        );

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(Map.class);

        if (response != null && response.get("response") != null) {
            return response.get("response").toString();
        }
        return "I'm having trouble responding right now.";
    }
}
