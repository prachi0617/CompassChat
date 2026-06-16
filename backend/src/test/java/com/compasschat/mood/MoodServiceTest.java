package com.compasschat.mood;

import com.compasschat.common.base.exception.ResourceNotFoundException;
import com.compasschat.common.enums.MoodType;
import com.compasschat.mood.ResourceRecommendationService.RecommendedResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MoodServiceTest {

    @Mock private MoodRepository moodRepo;
    @Mock private ResourceRecommendationService recommender;

    private MoodService service;

    private final UUID userId = UUID.randomUUID();
    private final UUID moodId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new MoodService(moodRepo, recommender);
    }

    // --- logMood() ---

    @Test
    void shouldReturnMoodLogResult_whenLogMoodWithNonDistressedMood() {
        Mood saved = new Mood(userId, MoodType.HAPPY, "feeling good");
        when(moodRepo.save(any(Mood.class))).thenReturn(saved);
        when(recommender.recommend(MoodType.HAPPY, "feeling good")).thenReturn(List.of());

        MoodService.MoodLogResult result = service.logMood(userId, MoodType.HAPPY, "feeling good");

        assertNotNull(result);
        assertSame(saved, result.mood());
        // No crisis resources for non-distressed mood
        assertTrue(result.resources().isEmpty());
    }

    @Test
    void shouldIncludeCrisisResources_whenLogMoodWithDistressedMood() {
        Mood saved = new Mood(userId, MoodType.DISTRESSED, "really bad day");
        when(moodRepo.save(any(Mood.class))).thenReturn(saved);
        when(recommender.recommend(MoodType.DISTRESSED, "really bad day")).thenReturn(List.of());

        MoodService.MoodLogResult result = service.logMood(userId, MoodType.DISTRESSED, "really bad day");

        // Crisis resources always added for distressed states (2 hardcoded crisis lines)
        assertTrue(result.resources().size() >= 2);
        assertTrue(result.resources().stream().anyMatch(r -> r.source().equals("crisis_line")));
    }

    @Test
    void shouldNotThrowAndReturnResult_whenRecommenderThrowsException() {
        Mood saved = new Mood(userId, MoodType.STRESSED, "overwhelmed");
        when(moodRepo.save(any(Mood.class))).thenReturn(saved);
        when(recommender.recommend(any(), any())).thenThrow(new RuntimeException("AI service down"));

        assertDoesNotThrow(() -> {
            MoodService.MoodLogResult result = service.logMood(userId, MoodType.STRESSED, "overwhelmed");
            assertNotNull(result.mood());
        });
    }

    @Test
    void shouldIncludeBothCrisisAndRecommenderResources_whenDistressedAndRecommenderSucceeds() {
        Mood saved = new Mood(userId, MoodType.OVERWHELMED, "can't cope");
        when(moodRepo.save(any(Mood.class))).thenReturn(saved);
        RecommendedResource rec = new RecommendedResource("Community Help", "Local support", "http://example.com", "community");
        when(recommender.recommend(MoodType.OVERWHELMED, "can't cope")).thenReturn(List.of(rec));

        MoodService.MoodLogResult result = service.logMood(userId, MoodType.OVERWHELMED, "can't cope");

        // 2 crisis lines + 1 recommender resource = 3
        assertEquals(3, result.resources().size());
    }

    // --- getHistoryForUser() ---

    @Test
    void shouldReturnPage_whenGetHistoryForUserAndRequesterMatchesOwner() {
        Page<Mood> page = new PageImpl<>(List.of());
        when(moodRepo.findByUserIdOrderByCreatedAtDesc(userId, Pageable.unpaged())).thenReturn(page);

        Page<Mood> result = service.getHistoryForUser(userId, userId, Pageable.unpaged());

        assertSame(page, result);
    }

    @Test
    void shouldThrowAccessDeniedException_whenGetHistoryForUserAndRequesterDiffers() {
        UUID otherId = UUID.randomUUID();

        assertThrows(AccessDeniedException.class,
                () -> service.getHistoryForUser(userId, otherId, Pageable.unpaged()));
    }

    // --- getOneForUser() ---

    @Test
    void shouldReturnMood_whenGetOneForUserAndOwnerMatches() {
        Mood mood = new Mood(userId, MoodType.CALM, "nice");
        when(moodRepo.findById(moodId)).thenReturn(Optional.of(mood));

        Mood result = service.getOneForUser(moodId, userId);

        assertSame(mood, result);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenGetOneForUserAndMoodMissing() {
        when(moodRepo.findById(moodId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getOneForUser(moodId, userId));
    }

    @Test
    void shouldThrowAccessDeniedException_whenGetOneForUserAndRequesterDiffers() {
        UUID otherId = UUID.randomUUID();
        Mood mood = new Mood(userId, MoodType.CALM, "nice");
        when(moodRepo.findById(moodId)).thenReturn(Optional.of(mood));

        assertThrows(AccessDeniedException.class,
                () -> service.getOneForUser(moodId, otherId));
    }
}
