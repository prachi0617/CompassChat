package com.compasschat.message;

import com.compasschat.common.base.BaseService;
import com.compasschat.common.enums.AuditAction;
import com.compasschat.common.enums.Role;
import com.compasschat.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MessageService extends BaseService<Message, UUID> {

    private final MessageRepository messages;
    private final MessageAuditLogRepository auditLogs;

    public MessageService(MessageRepository messages, MessageAuditLogRepository auditLogs) {
        super(messages, "Message");
        this.messages = messages;
        this.auditLogs = auditLogs;
    }

    // ----- WRITES -----

    @Transactional
    public Message post(UUID channelId, UUID senderId, String content) {
        if (!channelService.canUserAccess(channelId, senderId)) {
            throw new AccessDeniedException("Not a member of this channel");
        }
        Message msg = messages.save(new Message(senderId, channelId, content));
        auditLogs.save(new MessageAuditLog(
            msg.getId(), AuditAction.CREATED, senderId,
            null, content));
    return msg;
    }

    @Transactional
    public Message edit(UUID messageId, UUID requesterId, String newContent) {
        Message msg = findVisibleById(messageId);

        // Rule: only the original sender can edit their own message.
        // (We do NOT allow moderators to silently rewrite content -
        // that destroys trust. Moderators can delete; only authors edit.)
        if (!msg.getSenderId().equals(requesterId)) {
            throw new AccessDeniedException("Only the author can edit this message");
        }

        String previous = msg.getContent();
        msg.editContent(newContent); // domain method - keeps content + editedAt in sync

        auditLogs.save(new MessageAuditLog(
                msg.getId(), AuditAction.EDITED, requesterId,
                previous, newContent
        ));
        return msg;
    }

    @Transactional
    public void softDelete(UUID messageId, UUID requesterId, Role requesterRole) {
        Message msg = findVisibleById(messageId);

        // Rule: sender can delete their own; mods/admins can delete any.
        boolean isAuthor = msg.getSenderId().equals(requesterId);
        boolean isStaff = requesterRole == Role.MODERATOR || requesterRole == Role.ADMIN;
        if (!isAuthor && !isStaff) {
            throw new AccessDeniedException("Not allowed to delete this message");
        }

        // Audit FIRST. If this throws, the message stays intact.
        auditLogs.save(new MessageAuditLog(
                msg.getId(), AuditAction.DELETED, requesterId,
                msg.getContent(), null
        ));
        msg.markDeleted();
    }

    // ----- READS -----

    @Transactional(readOnly = true)
    public Page<Message> listChannelMessages(UUID channelId, Pageable pageable) {
        return messages.findByChannelIdAndDeletedFalseOrderByCreatedAtDesc(channelId, pageable);
    }

    @Transactional(readOnly = true)
    public List<MessageAuditLog> getHistory(UUID messageId) {
        // ensure the message exists (even if deleted - mods need history)
        if (!messages.existsById(messageId)) {
            throw new ResourceNotFoundException("Message", messageId.toString());
        }
        return auditLogs.findByMessageIdOrderByCreatedAtAsc(messageId);
    }

    // ----- HELPERS -----

    /** Inherited findById ignores the soft-delete flag; this wrapper
     *  enforces "if it's crumpled, pretend it doesn't exist" for
     *  normal callers. Moderator code paths use findById directly. */
    private Message findVisibleById(UUID id) {
        Message msg = findById(id); // throws ResourceNotFound if missing
        if (msg.isDeleted()) {
            throw new ResourceNotFoundException("Message", id.toString());
        }
        return msg;
    }
}
