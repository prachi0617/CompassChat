package com.compasschat.ai.context.parsers;

import com.compasschat.ai.context.IntentType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntentParserTest {

    private final IntentParser parser = new IntentParser();

    @Test
    void shouldReturnEscalate_whenUserRequestsHumanAgent() {
        assertEquals(IntentType.ESCALATE, parser.parse("I need a human agent right now"));
    }

    @Test
    void shouldReturnEscalate_whenUserWantsToTalkToSomeone() {
        assertEquals(IntentType.ESCALATE, parser.parse("I want to talk to someone please"));
    }

    @Test
    void shouldReturnHousing_whenRentKeywordPresent() {
        assertEquals(IntentType.HOUSING, parser.parse("I need help with rent this month"));
    }

    @Test
    void shouldReturnHousing_whenShelterKeywordPresent() {
        assertEquals(IntentType.HOUSING, parser.parse("looking for shelter tonight"));
    }

    @Test
    void shouldReturnYouth_whenJobKeywordPresent() {
        assertEquals(IntentType.YOUTH, parser.parse("looking for a job and career training"));
    }

    @Test
    void shouldReturnCivic_whenWilmingtonKeywordPresent() {
        assertEquals(IntentType.CIVIC, parser.parse("wilmington city council vote results"));
    }

    @Test
    void shouldReturnResources_whenFoodKeywordPresent() {
        assertEquals(IntentType.RESOURCES, parser.parse("food assistance and snap benefits"));
    }

    @Test
    void shouldReturnMood_whenSadKeywordPresent() {
        assertEquals(IntentType.MOOD, parser.parse("I feel so sad and lonely today"));
    }

    @Test
    void shouldReturnReminder_whenAppointmentKeywordPresent() {
        assertEquals(IntentType.REMINDER, parser.parse("remind me about my appointment tomorrow"));
    }

    @Test
    void shouldReturnCaseworker_whenIntakeKeywordPresent() {
        assertEquals(IntentType.CASEWORKER, parser.parse("client intake and referral status update"));
    }

    @Test
    void shouldReturnUnknown_whenNoKeywordsMatch() {
        assertEquals(IntentType.UNKNOWN, parser.parse("hello world"));
    }

    @Test
    void shouldBeCaseInsensitive_whenInputIsUpperCase() {
        assertEquals(IntentType.HOUSING, parser.parse("I NEED HELP WITH RENT"));
    }
}
