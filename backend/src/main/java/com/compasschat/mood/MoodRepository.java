package com.compasschat.mood;

import com.compasschat.common.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Note: there is intentionally NO "find all moods" query and NO
 * "search moods by content" query. Mood data is private. Queries
 * are always scoped to a specific user, period.
 */
public interface MoodRepository extends BaseRepository<Mood, UUID> {

    Page<Mood> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<Mood> findByUserIdAndCreatedAtBetweenOrderByCreatedAtAsc(
            UUID userId, LocalDateTime from, LocalDateTime to);

    long countByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime after);
}
