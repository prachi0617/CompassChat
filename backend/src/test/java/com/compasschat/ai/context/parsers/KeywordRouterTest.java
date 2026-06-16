package com.compasschat.ai.context.parsers;

import com.compasschat.ai.context.ContextFragment;
import com.compasschat.ai.context.ContextSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeywordRouterTest {

    private final KeywordRouter router = new KeywordRouter();

    @Test
    void shouldReturnWellbeingFragment_whenMentalHealthKeywordPresent() {
        ContextFragment fragment = router.route("I need mental health counseling");

        assertNotNull(fragment);
        assertEquals(ContextSource.WELLBEING, fragment.source());
    }

    @Test
    void shouldReturnYouthFragment_whenChildKeywordPresent() {
        ContextFragment fragment = router.route("I have a child and need family support");

        assertNotNull(fragment);
        assertEquals(ContextSource.YOUTH, fragment.source());
    }

    @Test
    void shouldReturnCivicFragment_whenLegalKeywordPresent() {
        ContextFragment fragment = router.route("I need legal rights and an attorney");

        assertNotNull(fragment);
        assertEquals(ContextSource.CIVIC, fragment.source());
    }

    @Test
    void shouldReturnResourceDirectoryFragment_whenNoKeywordsMatch() {
        ContextFragment fragment = router.route("general question about things");

        assertNotNull(fragment);
        assertEquals(ContextSource.RESOURCE_DIRECTORY, fragment.source());
    }

    @Test
    void shouldNeverReturnNull_whenInputIsEmpty() {
        ContextFragment fragment = router.route("");

        assertNotNull(fragment);
    }
}
