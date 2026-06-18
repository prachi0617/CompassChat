package com.compasschat.ai.context;

import com.compasschat.ai.context.parsers.IntentParser;
import com.compasschat.ai.context.parsers.KeywordRouter;
import com.compasschat.ai.context.parsers.SentimentParser;
import com.compasschat.ai.integration.CivicContextClient;
import com.compasschat.ai.integration.HousingContextClient;
import com.compasschat.ai.integration.KindConnectClient;
import com.compasschat.ai.integration.YouthPathwayClient;
import com.compasschat.ai.service.ResourceSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AIContextBuilderTest {

    @Mock private IntentParser intentParser;
    @Mock private SentimentParser sentimentParser;
    @Mock private KeywordRouter keywordRouter;
    @Mock private HousingContextClient housingClient;
    @Mock private YouthPathwayClient youthClient;
    @Mock private KindConnectClient kindConnectClient;
    @Mock private CivicContextClient civicClient;
    @Mock private ResourceSearchService resourceSearchService;

    private AIContextBuilder builder;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        builder = new AIContextBuilder(intentParser, sentimentParser, keywordRouter,
                housingClient, youthClient, kindConnectClient, civicClient, resourceSearchService);
    }

    private void stubSentiment(double score) {
        when(sentimentParser.score(anyString())).thenReturn(score);
    }

    // --- build(): intent switch cases ---

    @Test
    void shouldCallHousingClientAndAddFragment_whenIntentIsHousing() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.HOUSING);
        stubSentiment(0.0);
        when(housingClient.getContext(anyString())).thenReturn("housing info");

        AIContext context = builder.build("rent help", userId);

        assertEquals(IntentType.HOUSING, context.getIntent());
        assertEquals(1, context.getFragments().size());
        assertEquals(ContextSource.HOUSING, context.getFragments().get(0).source());
        assertEquals("housing info", context.getFragments().get(0).content());
    }

    @Test
    void shouldCallYouthClientAndAddFragment_whenIntentIsYouth() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.YOUTH);
        stubSentiment(0.0);
        when(youthClient.getContext(anyString())).thenReturn("youth info");

        AIContext context = builder.build("job training", userId);

        assertEquals(ContextSource.YOUTH, context.getFragments().get(0).source());
    }

    @Test
    void shouldCallCivicClientAndAddFragment_whenIntentIsCivic() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.CIVIC);
        stubSentiment(0.0);
        when(civicClient.getRelevantContext(anyString())).thenReturn("civic info");

        AIContext context = builder.build("city council", userId);

        assertEquals(ContextSource.CIVIC, context.getFragments().get(0).source());
    }

    @Test
    void shouldCallResourceSearchAndAddFragment_whenIntentIsResources() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.RESOURCES);
        stubSentiment(0.0);
        when(resourceSearchService.search(anyString())).thenReturn("food bank info");

        AIContext context = builder.build("food assistance", userId);

        assertEquals(ContextSource.RESOURCE_DIRECTORY, context.getFragments().get(0).source());
    }

    @Test
    void shouldCallKindConnectAndAddFragment_whenIntentIsMood() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.MOOD);
        stubSentiment(0.0);
        when(kindConnectClient.getContext(anyString())).thenReturn("wellbeing info");

        AIContext context = builder.build("feeling sad", userId);

        assertEquals(ContextSource.WELLBEING, context.getFragments().get(0).source());
    }

    @Test
    void shouldAddStaticReminderFragment_whenIntentIsReminder() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.REMINDER);
        stubSentiment(0.0);

        AIContext context = builder.build("remind me", userId);

        assertEquals(ContextSource.REMINDER, context.getFragments().get(0).source());
        verify(kindConnectClient, never()).getContext(anyString());
    }

    @Test
    void shouldAddStaticCaseworkerFragment_whenIntentIsCaseworker() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.CASEWORKER);
        stubSentiment(0.0);

        AIContext context = builder.build("client intake", userId);

        assertEquals(ContextSource.CASEWORKER, context.getFragments().get(0).source());
    }

    @Test
    void shouldSetEscalationAndAddNoFragment_whenIntentIsEscalate() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.ESCALATE);
        stubSentiment(0.0);

        AIContext context = builder.build("I need a human", userId);

        assertTrue(context.getEscalation().escalate());
        assertEquals("user_requested", context.getEscalation().reason());
        assertTrue(context.getFragments().isEmpty());
    }

    @Test
    void shouldCallKeywordRouterAndAddFragment_whenIntentIsUnknown() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.UNKNOWN);
        stubSentiment(0.0);
        ContextFragment routedFragment = new ContextFragment(ContextSource.RESOURCE_DIRECTORY, "general help");
        when(keywordRouter.route(anyString())).thenReturn(routedFragment);

        AIContext context = builder.build("hello", userId);

        verify(keywordRouter).route(anyString());
        assertEquals(1, context.getFragments().size());
    }

    // --- build(): sentiment escalation override ---

    @Test
    void shouldSetHighRiskEscalation_whenSentimentBelowThresholdAndNotAlreadyEscalated() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.HOUSING);
        when(housingClient.getContext(anyString())).thenReturn("housing");
        stubSentiment(-0.9);  // below -0.7 threshold

        AIContext context = builder.build("I am homeless and suicidal", userId);

        assertTrue(context.getEscalation().escalate());
        assertEquals("high_risk_sentiment", context.getEscalation().reason());
    }

    @Test
    void shouldNotOverrideEscalation_whenAlreadyEscalatedByIntent() {
        // ESCALATE intent sets escalation first; bad sentiment should NOT override it
        when(intentParser.parse(anyString())).thenReturn(IntentType.ESCALATE);
        stubSentiment(-0.9);

        AIContext context = builder.build("I need a human agent", userId);

        // Escalation reason stays as user_requested, not overridden to high_risk_sentiment
        assertEquals("user_requested", context.getEscalation().reason());
    }

    @Test
    void shouldNotEscalate_whenSentimentIsExactlyAtThreshold() {
        when(intentParser.parse(anyString())).thenReturn(IntentType.UNKNOWN);
        stubSentiment(-0.7);  // exactly at threshold — NOT below, so no override
        ContextFragment f = new ContextFragment(ContextSource.RESOURCE_DIRECTORY, "help");
        when(keywordRouter.route(anyString())).thenReturn(f);

        AIContext context = builder.build("I am scared", userId);

        assertFalse(context.getEscalation().escalate());
    }

    // --- buildSystemPrompt() ---

    @Test
    void shouldIncludeFragmentInPrompt_whenContextHasFragments() {
        AIContext context = new AIContext();
        context.addFragment(new ContextFragment(ContextSource.HOUSING, "Available shelter: 123 Main St"));

        String prompt = builder.buildSystemPrompt(context);

        assertTrue(prompt.contains("Relevant context"));
        assertTrue(prompt.contains("Available shelter: 123 Main St"));
        assertTrue(prompt.contains("HOUSING"));
    }

    @Test
    void shouldNotIncludeRelevantContextSection_whenNoFragments() {
        AIContext context = new AIContext();

        String prompt = builder.buildSystemPrompt(context);

        assertFalse(prompt.contains("Relevant context"));
    }

    @Test
    void shouldIncludeEscalationWarning_whenContextIsEscalated() {
        AIContext context = new AIContext();
        context.setEscalation(EscalationDecision.highRisk());

        String prompt = builder.buildSystemPrompt(context);

        assertTrue(prompt.contains("IMPORTANT"));
    }

    @Test
    void shouldIncludeBothFragmentsAndEscalation_whenContextHasBoth() {
        AIContext context = new AIContext();
        context.addFragment(new ContextFragment(ContextSource.WELLBEING, "Call 988"));
        context.setEscalation(EscalationDecision.requestedByUser());

        String prompt = builder.buildSystemPrompt(context);

        assertTrue(prompt.contains("Relevant context"));
        assertTrue(prompt.contains("Call 988"));
        assertTrue(prompt.contains("IMPORTANT"));
    }

    // --- buildSystemPromptWithHistory() ---

    @Test
    void shouldAppendHistory_whenHistoryIsNonEmpty() {
        AIContext context = new AIContext();

        String prompt = builder.buildSystemPromptWithHistory(context, List.of("hello", "Hi there!"));

        assertTrue(prompt.contains("Conversation history"));
        assertTrue(prompt.contains("User: hello"));
        assertTrue(prompt.contains("AI: Hi there!"));
    }

    @Test
    void shouldReturnBasePrompt_whenHistoryIsNull() {
        AIContext context = new AIContext();

        String prompt = builder.buildSystemPromptWithHistory(context, null);
        String base = builder.buildSystemPrompt(context);

        assertEquals(base, prompt);
    }

    @Test
    void shouldReturnBasePrompt_whenHistoryIsEmpty() {
        AIContext context = new AIContext();

        String prompt = builder.buildSystemPromptWithHistory(context, List.of());
        String base = builder.buildSystemPrompt(context);

        assertEquals(base, prompt);
    }
}
