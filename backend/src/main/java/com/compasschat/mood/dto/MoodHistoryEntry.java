package com.compasschat.mood.dto;

import com.compasschat.common.enums.MoodType;
import com.compasschat.common.utils.DateUtil;
import com.compasschat.mood.Mood;

import java.time.LocalDateTime;
import java.util.UUID;

public record MoodHistoryEntry(
        UUID id,
        MoodType moodType,
        String note,
        LocalDateTime loggedAt,
        String timeAgo
) {
    public static MoodHistoryEntry from(Mood m) {
        return new MoodHistoryEntry(
                m.getId(),
                m.getMoodType(),
                m.getNote(),
                m.getCreatedAt(),
                DateUtil.timeAgo(m.getCreatedAt())  // <- DateUtil paying rent
        );
    }
}
