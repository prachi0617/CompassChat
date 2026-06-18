package com.compasschat.ai.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OllamaProviderTest {

    @Test
    void shouldBeDisabled_whenApiUrlIsBlank() {
        OllamaProvider provider = new OllamaProvider("", "gemma2:2b");
        assertFalse(provider.isEnabled());
    }

    @Test
    void shouldBeDisabled_whenApiUrlIsNull() {
        OllamaProvider provider = new OllamaProvider(null, "gemma2:2b");
        assertFalse(provider.isEnabled());
    }

    @Test
    void shouldBeEnabled_whenApiUrlIsSet() {
        OllamaProvider provider = new OllamaProvider("http://localhost:11434/api/generate", "gemma2:2b");
        assertTrue(provider.isEnabled());
    }

    @Test
    void shouldThrow_whenCalledWhileDisabled() {
        OllamaProvider provider = new OllamaProvider("", "gemma2:2b");
        assertThrows(NullPointerException.class,
                () -> provider.complete("system", "user"));
    }
}
