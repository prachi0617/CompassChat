package com.compasschat.ai.context;

import com.compasschat.ai.context.parsers.IntentParser;
import com.compasschat.ai.context.parsers.KeywordRouter;
import com.compasschat.ai.context.parsers.SentimentParser;
import com.compasschat.ai.integration.CivicContextClient;
import com.compasschat.ai.integration.HousingContextClient;
import com.compasschat.ai.integration.KindConnectClient;
import com.compasschat.ai.integration.YouthPathwayClient;
import com.compasschat.ai.service.ResourceSearchService;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AIContextBuilder {

    private final IntentParser intentParser;
    private final SentimentParser sentimentParser;
    private final KeywordRouter keywordRouter;
    private final HousingContextClient housingClient;
    private final YouthPathwayClient youthClient;
    private final KindConnectClient kindConnectClient;
    private final CivicContextClient civicClient;
    private final ResourceSearchService resourceSearchService;

    public AIContextBuilder(IntentParser intentParser,
                            SentimentParser sentimentParser,
                            KeywordRouter keywordRouter,
                            HousingContextClient housingClient,
                            YouthPathwayClient youthClient,
                            KindConnectClient kindConnectClient,
                            CivicContextClient civicClient,
                            ResourceSearchService resourceSearchService) {
        this.intentParser = intentParser;
        this.sentimentParser = sentimentParser;
        this.keywordRouter = keywordRouter;
        this.housingClient = housingClient;
        this.youthClient = youthClient;
        this.kindConnectClient = kindConnectClient;
        this.civicClient = civicClient;
        this.resourceSearchService = resourceSearchService;
    }

    public AIContext build(String userMessage, UUID userId) {
        AIContext context = new AIContext();
        context.setUserMessage(userMessage);

        IntentType intent = intentParser.parse(userMessage);
        context.setIntent(intent);

        double sentiment = sentimentParser.score(userMessage);
        context.setSentiment(sentiment);

        switch (intent) {
            case HOUSING ->
                context.addFragment(new ContextFragment(ContextSource.HOUSING,
                        housingClient.getContext(userMessage)));
            case YOUTH ->
                context.addFragment(new ContextFragment(ContextSource.YOUTH,
                        youthClient.getContext(userMessage)));
            case CIVIC ->
                context.addFragment(new ContextFragment(ContextSource.CIVIC,
                        civicClient.getRelevantContext(userMessage)));
            case RESOURCES ->
                context.addFragment(new ContextFragment(ContextSource.RESOURCE_DIRECTORY,
                        resourceSearchService.search(userMessage)));
            case MOOD ->
                context.addFragment(new ContextFragment(ContextSource.WELLBEING,
                        kindConnectClient.getContext(userMessage)));
            case REMINDER ->
                context.addFragment(new ContextFragment(ContextSource.REMINDER,
                        "Reminder support is available. Describe the appointment or task to set a reminder."));
            case CASEWORKER ->
                context.addFragment(new ContextFragment(ContextSource.CASEWORKER,
                        "Caseworker workflow tools are available. You can look up client status or create a referral."));
            case ESCALATE ->
                context.setEscalation(EscalationDecision.requestedByUser());
            default ->
                context.addFragment(keywordRouter.route(userMessage));
        }

        // High-risk sentiment overrides intent-based escalation decision
        if (sentiment < -0.7 && !context.getEscalation().escalate()) {
            context.setEscalation(EscalationDecision.highRisk());
        }

        return context;
    }

    /** Builds a system prompt string from context fragments for the AI provider. */
    public String buildSystemPrompt(AIContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are CompassChat, a community resource assistant serving Delaware residents. ");
        sb.append("Be concise, empathetic, and actionable.\n\n");

        if (!context.getFragments().isEmpty()) {
            sb.append("Relevant context:\n");
            for (ContextFragment fragment : context.getFragments()) {
                sb.append("--- ").append(fragment.source()).append(" ---\n");
                sb.append(fragment.content()).append("\n\n");
            }
        }

        if (context.getEscalation().escalate()) {
            sb.append("IMPORTANT: This user may need immediate human support. ");
            sb.append("Acknowledge their situation warmly and offer to connect them with a live agent.\n");
        }

        return sb.toString();
    }
}
