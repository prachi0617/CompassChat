package com.compasschat.ai.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AIContext {

    private String userMessage;
    private IntentType intent = IntentType.UNKNOWN;
    private double sentiment = 0.0;
    private final List<ContextFragment> fragments = new ArrayList<>();
    private EscalationDecision escalation = EscalationDecision.none();

    public void addFragment(ContextFragment fragment) {
        if (fragment != null) fragments.add(fragment);
    }

    public String getUserMessage()             { return userMessage; }
    public void setUserMessage(String msg)     { this.userMessage = msg; }

    public IntentType getIntent()              { return intent; }
    public void setIntent(IntentType intent)   { this.intent = intent; }

    public double getSentiment()               { return sentiment; }
    public void setSentiment(double sentiment) { this.sentiment = sentiment; }

    public List<ContextFragment> getFragments(){ return Collections.unmodifiableList(fragments); }

    public EscalationDecision getEscalation()                  { return escalation; }
    public void setEscalation(EscalationDecision escalation)   { this.escalation = escalation; }
}
