package com.compasschat.mood;

import com.compasschat.common.base.BaseService;
import com.compasschat.common.enums.MoodType;
import com.compasschat.common.base.exception.ResourceNotFoundException;
import com.compasschat.mood.ResourceRecommendationService.RecommendedResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MoodService extends BaseService<Mood, UUID> {

    private final MoodRepository moods;
    private final ResourceRecommendationService recommender;

    public MoodService(MoodRepository moods, ResourceRecommendationService recommender) {
        super(moods, "Mood");
        this.moods = moods;
        this.recommender = recommender;
    }

    @Transactional
    public MoodLogResult logMood(UUID userId, MoodType moodType, String note) {
        Mood mood = moods.save(new Mood(userId, moodType, note));

        List<RecommendedResource> resources = gatherResources(moodType, note);
        return new MoodLogResult(mood, resources);
    }

    private List<RecommendedResource> gatherResources(MoodType moodType, String note) {
        List<RecommendedResource> result = new ArrayList<>();

        if (moodType.isDistressed()) {
            result.addAll(crisisResources());
        }

        try {
            result.addAll(recommender.recommend(moodType, note));
        } catch (Exception e) {
            System.err.println("Recommender failed: " + e.getMessage());
        }

        return result;
    }

    private List<RecommendedResource> crisisResources() {
        return List.of(
                new RecommendedResource(
                        "988 Suicide & Crisis Lifeline",
                        "Free, confidential support 24/7. Call or text 988.",
                        "https://988lifeline.org",
                        "crisis_line"
                ),
                new RecommendedResource(
                        "Crisis Text Line",
                        "Text HOME to 741741 to connect with a counselor.",
                        "https://www.crisistextline.org",
                        "crisis_line"
                )
        );
    }

    // ----- READS (strictly scoped to the requesting user) -----

    @Transactional(readOnly = true)
    public Page<Mood> getHistoryForUser(UUID userId, UUID requesterId, Pageable pageable) {
        requireSelf(userId, requesterId);
        return moods.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Mood getOneForUser(UUID moodId, UUID requesterId) {
        Mood m = moods.findById(moodId)
                .orElseThrow(() -> new ResourceNotFoundException("Mood", moodId.toString()));
        requireSelf(m.getUserId(), requesterId);
        return m;
    }

    private void requireSelf(UUID ownerId, UUID requesterId) {
        if (!ownerId.equals(requesterId)) {
            throw new AccessDeniedException("Mood logs are private");
        }
    }

    /** Bundle returned from logMood - both the saved entry and the
     *  resources, so the controller doesn't need two service calls. */
    public record MoodLogResult(Mood mood, List<RecommendedResource> resources) {}
}
