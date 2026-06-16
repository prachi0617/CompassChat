package com.compasschat.dm;

import com.compasschat.common.base.BaseService;
import com.compasschat.common.base.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DirectMessageService extends BaseService<DirectMessage, UUID> {

    private final DirectMessageRepository directMessages;

    public DirectMessageService(DirectMessageRepository directMessages) {
        super(directMessages, "DirectMessage");
        this.directMessages = directMessages;
    }

    @Transactional
    public DirectMessage send(UUID senderId, UUID recipientId, String content) {
        return directMessages.save(new DirectMessage(senderId, recipientId, content));
    }

    @Transactional(readOnly = true)
    public Page<DirectMessage> getConversation(UUID requesterId, UUID otherUserId, Pageable pageable) {
        if (!requesterId.equals(otherUserId)) {
            // only participants may read the conversation
            // (requesterId IS a participant — they are reading their own conversation with otherUserId)
        }
        return directMessages.findConversation(requesterId, otherUserId, pageable);
    }

    @Transactional
    public void softDelete(UUID messageId, UUID requesterId) {
        DirectMessage dm = findById(messageId);
        if (dm.isDeleted()) {
            throw new ResourceNotFoundException("DirectMessage", messageId.toString());
        }
        if (!dm.getSenderId().equals(requesterId)) {
            throw new AccessDeniedException("Only the sender can delete this message");
        }
        dm.markDeleted();
    }
}
