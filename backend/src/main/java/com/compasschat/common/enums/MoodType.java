package com.compasschat.common.enums;

public enum MoodType {

    HAPPY(false),
    HOPEFUL(false),
    CALM(false),
    GRATEFUL(false),

    NEUTRAL(false),
    TIRED(false),

    SAD(false),
    LONELY(false),
    ANXIOUS(false),
    ANGRY(false),
    STRESSED(false),

    /** Heavier states: trigger crisis resource pathway. */
    OVERWHELMED(true),
    DISTRESSED(true);

    private final boolean distressed;

    MoodType(boolean distressed) {
        this.distressed = distressed;
    }

    public boolean isDistressed() {
        return distressed;
    }
}