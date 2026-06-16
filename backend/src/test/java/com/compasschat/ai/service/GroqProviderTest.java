package com.compasschat.ai.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroqProviderTest {

    @Test
    void shouldBeDisabled_whenApiKeyIsBlank() {
        GroqProvider provider = new GroqProvider(null, "");
        assertFalse(provider.isEnabled());
    }

    @Test
    void shouldBeDisabled_whenApiKeyIsNull() {
        GroqProvider provider = new GroqProvider(null, null);
        assertFalse(provider.isEnabled());
    }

    @Test
    void shouldBeDisabled_whenApiKeyPresentButBuilderIsNull() {
        GroqProvider provider = new GroqProvider(null, "some-api-key");
        assertFalse(provider.isEnabled());
    }

    @Test
    void shouldThrowUnsupportedOperationException_whenCompleteCalledWhileDisabled() {
        GroqProvider provider = new GroqProvider(null, "");
        assertThrows(UnsupportedOperationException.class,
                () -> provider.complete("system", "user message"));
    }
}
