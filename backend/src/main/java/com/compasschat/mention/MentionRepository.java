package com.compasschat.mention;

import com.compasschat.common.base.BaseRepository;

import java.util.List;
import java.util.UUID;

public interface MentionRepository extends BaseRepository<Mention, UUID> {

    List<Mention> findByMentionedUserIdOrderByCreatedAtDesc(UUID mentionedUserId);

    List<Mention> findByMentionedUserIdAndReadFalseOrderByCreatedAtDesc(UUID mentionedUserId);

    long countByMentionedUserIdAndReadFalse(UUID mentionedUserId);
}
