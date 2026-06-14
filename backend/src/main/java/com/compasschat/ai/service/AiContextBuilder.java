package com.compasschat.ai.context;

import com.compasschat.ai.dto.AIRequest;
import com.compasschat.ai.dto.AIContext;
import com.compasschat.ai.context.parsers.*;
import com.compasschat.housing.HousingChatContextService;
import com.compasschat.youth.YouthChatContextService;
import com.compasschat.resource.ResourceSearchService;
import com.compasschat.civic.CivicBriefingService;
import com.compasschat.wellbeing.WellbeingService;
import com.compasschat.reminders.ReminderService;

public class AIContextBuilder {

    private final IntentParser intentParser;
    private final SentimentParser sentimentParser;
    private final KeywordRouter keywordRouter;

    private final HousingChatContextService housingService;
    private final YouthChatContextService youthService;
    private final ResourceSearchService resourceService;
    private final CivicBriefingService civicService;
    private final WellbeingService wellbeingService;
    private final ReminderService reminderService;

    public AIContextBuilder(
            IntentParser intentParser,
            SentimentParser sentimentParser,
            KeywordRouter keywordRouter,
            HousingChatContextService housingService,
            YouthChatContextService youthService,
            ResourceSearchService resourceService,
            CivicBriefingService civicService,
            WellbeingService wellbeingService,
            ReminderService reminderService
    ) {
        this.intentParser = intentParser;
        this.sentimentParser = sentimentParser;
        this.keywordRouter = keywordRouter;
        this.housingService = housingService;
        this.youthService = youthService;
        this.resourceService = resourceService;
        this.civicService = civicService;
        this.wellbeingService = wellbeingService;
        this.reminderService = reminderService;
    }

    public AIContext build(String userMessage, Long userId) {

        IntentType intent = intentParser.parse(userMessage);
        double sentiment = sentimentParser.score(userMessage);

        AIContext context = new AIContext();
        context.setUserMessage(userMessage);
        context.setIntent(intent);
        context.setSentiment(sentiment);

        switch (intent) {

            case HOUSING:
                context.addFragment(housingService.buildContext(userId, userMessage));
                break;

            case YOUTH:
                context.addFragment(youthService.buildContext(userId));
                break;

            case RESOURCES:
                context.addFragment(resourceService.search(userMessage));
                break;

            case CIVIC:
                context.addFragment(civicService.getLatestBriefings());
                break;

            case MOOD:
                context.addFragment(wellbeingService.buildMoodContext(userId, sentiment));
                break;

            case REMINDER:
                context.addFragment(reminderService.buildReminderContext(userId, userMessage));
                break;

            case ESCALATE:
                context.setEscalation(EscalationDecision.requestedByUser());
                break;

            default:
                context.addFragment(keywordRouter.route(userMessage));
        }

        // Automatic escalation for high-risk sentiment
        if (sentiment < -0.7) {
            context.setEscalation(EscalationDecision.highRisk());
        }

        return context;
    }
}
