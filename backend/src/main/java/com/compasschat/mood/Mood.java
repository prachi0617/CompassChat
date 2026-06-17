package com.compasschat.mood;

import com.compasschat.common.base.AuditableEntity;
import com.compasschat.common.enums.MoodType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(
        name = "moods",
        indexes = {
                @Index(name = "idx_moods_user_created", columnList = "userId, createdAt DESC")
        }
)
public class Mood extends AuditableEntity {

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private MoodType moodType;

    @Column(length = 1000)
    private String note;

    protected Mood() {} // JPA

    public Mood(UUID userId, MoodType moodType, String note) {
        this.userId = userId;
        this.moodType = moodType;
        this.note = note;
    }

    // Getters only
    public UUID getUserId() { return userId; }
    public MoodType getMoodType() { return moodType; }
    public String getNote() { return note; }
}
