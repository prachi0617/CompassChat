package com.compasschat.message;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.compasschat.common.base.BaseRepository;


public interface MessageAuditLogRepository extends BaseRepository<MessageAuditLog, UUID> {

    List<MessageAuditLog> findByMessageIdOrderByCreatedAtAsc(UUID messageId);

    Page<MessageAuditLog> findByPerformedByOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}

