package com.compasschat.message;

import com.compasschat.common.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MessageRepository extends BaseRepository<Message, UUID> {

    Page<Message> findByChannelIdAndDeletedFalseOrderByCreatedAtDesc(UUID channelId, Pageable pageable);

    Page<Message> findBySenderIdAndDeletedFalseOrderByCreatedAtDesc(UUID senderId, Pageable pageable);

    /** Moderator-only: includes soft-deleted. */
    Page<Message> findByChannelId(UUID channelId, Pageable pageable);

    long countByChannelIdAndCreatedAtAfterAndDeletedFalse(UUID channelId, LocalDateTime since);
}
