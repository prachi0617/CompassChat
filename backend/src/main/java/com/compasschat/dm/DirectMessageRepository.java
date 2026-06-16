package com.compasschat.dm;

import com.compasschat.common.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DirectMessageRepository extends BaseRepository<DirectMessage, UUID> {

    @Query("""
            SELECT dm FROM DirectMessage dm
            WHERE dm.deleted = false
              AND (
                (dm.senderId = :userId AND dm.recipientId = :otherId) OR
                (dm.senderId = :otherId AND dm.recipientId = :userId)
              )
            ORDER BY dm.createdAt DESC
            """)
    Page<DirectMessage> findConversation(
            @Param("userId") UUID userId,
            @Param("otherId") UUID otherId,
            Pageable pageable);
}
