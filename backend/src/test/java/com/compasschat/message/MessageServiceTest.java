package com.compasschat.message;

import com.compasschat.channel.ChannelService;
import com.compasschat.common.base.exception.ResourceNotFoundException;
import com.compasschat.common.enums.AuditAction;
import com.compasschat.common.enums.Role;
import com.compasschat.mention.MentionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
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
class MessageServiceTest {

    @Mock private MessageRepository messageRepo;
    @Mock private MessageAuditLogRepository auditLogRepo;
    @Mock private ChannelService channelService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private MentionService mentionService;

    private MessageService service;

    private final UUID senderId = UUID.randomUUID();
    private final UUID channelId = UUID.randomUUID();
    private final UUID messageId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new MessageService(messageRepo, auditLogRepo, channelService, eventPublisher, mentionService);
    }

    // --- post() ---

    @Test
    void shouldSaveMessageAndPublishEvent_whenPostAndAccessGranted() {
        when(channelService.canUserAccess(channelId, senderId)).thenReturn(true);
        Message saved = new Message(senderId, channelId, "Hello");
        when(messageRepo.save(any(Message.class))).thenReturn(saved);
        when(auditLogRepo.save(any())).thenReturn(null);

        Message result = service.post(channelId, senderId, "Hello");

        assertSame(saved, result);
        verify(auditLogRepo).save(any(MessageAuditLog.class));
        verify(eventPublisher).publishEvent(any(MessagePostedEvent.class));
        verify(mentionService).extractAndSave(any(), eq(channelId), eq(senderId), eq("Hello"));
    }

    @Test
    void shouldThrowAccessDeniedException_whenPostAndAccessDenied() {
        when(channelService.canUserAccess(channelId, senderId)).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> service.post(channelId, senderId, "Hello"));
        verify(messageRepo, never()).save(any());
    }

    // --- edit() ---

    @Test
    void shouldUpdateContentAndSaveAuditLog_whenEditAndRequesterIsAuthor() {
        Message msg = new Message(senderId, channelId, "original");
        when(messageRepo.findById(messageId)).thenReturn(Optional.of(msg));

        service.edit(messageId, senderId, "updated");

        assertEquals("updated", msg.getContent());
        ArgumentCaptor<MessageAuditLog> captor = ArgumentCaptor.forClass(MessageAuditLog.class);
        verify(auditLogRepo).save(captor.capture());
        assertEquals(AuditAction.EDITED, captor.getValue().getAction());
        assertEquals("original", captor.getValue().getPreviousContent());
        assertEquals("updated", captor.getValue().getNewContent());
    }

    @Test
    void shouldThrowAccessDeniedException_whenEditAndRequesterIsNotAuthor() {
        UUID otherUser = UUID.randomUUID();
        Message msg = new Message(senderId, channelId, "original");
        when(messageRepo.findById(messageId)).thenReturn(Optional.of(msg));

        assertThrows(AccessDeniedException.class,
                () -> service.edit(messageId, otherUser, "updated"));
    }

    @Test
    void shouldThrowResourceNotFoundException_whenEditAndMessageIsDeleted() {
        Message deleted = new Message(senderId, channelId, "deleted text");
        deleted.markDeleted();
        when(messageRepo.findById(messageId)).thenReturn(Optional.of(deleted));

        assertThrows(ResourceNotFoundException.class,
                () -> service.edit(messageId, senderId, "updated"));
    }

    @Test
    void shouldThrowResourceNotFoundException_whenEditAndMessageDoesNotExist() {
        when(messageRepo.findById(messageId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.edit(messageId, senderId, "updated"));
    }

    // --- softDelete() ---

    @Test
    void shouldMarkDeleted_whenSoftDeleteAndRequesterIsAuthor() {
        Message msg = new Message(senderId, channelId, "content");
        when(messageRepo.findById(messageId)).thenReturn(Optional.of(msg));

        service.softDelete(messageId, senderId, Role.MEMBER);

        assertTrue(msg.isDeleted());
        ArgumentCaptor<MessageAuditLog> captor = ArgumentCaptor.forClass(MessageAuditLog.class);
        verify(auditLogRepo).save(captor.capture());
        assertEquals(AuditAction.DELETED, captor.getValue().getAction());
    }

    @Test
    void shouldMarkDeleted_whenSoftDeleteAndRequesterIsModerator() {
        UUID modId = UUID.randomUUID();
        Message msg = new Message(senderId, channelId, "content");
        when(messageRepo.findById(messageId)).thenReturn(Optional.of(msg));

        service.softDelete(messageId, modId, Role.MODERATOR);

        assertTrue(msg.isDeleted());
    }

    @Test
    void shouldMarkDeleted_whenSoftDeleteAndRequesterIsAdmin() {
        UUID adminId = UUID.randomUUID();
        Message msg = new Message(senderId, channelId, "content");
        when(messageRepo.findById(messageId)).thenReturn(Optional.of(msg));

        service.softDelete(messageId, adminId, Role.ADMIN);

        assertTrue(msg.isDeleted());
    }

    @Test
    void shouldThrowAccessDeniedException_whenSoftDeleteAndRequesterIsMemberAndNotAuthor() {
        UUID otherId = UUID.randomUUID();
        Message msg = new Message(senderId, channelId, "content");
        when(messageRepo.findById(messageId)).thenReturn(Optional.of(msg));

        assertThrows(AccessDeniedException.class,
                () -> service.softDelete(messageId, otherId, Role.MEMBER));
    }

    // --- listChannelMessages() ---

    @Test
    void shouldDelegateToRepository_whenListChannelMessagesCalled() {
        Page<Message> page = new PageImpl<>(List.of());
        when(messageRepo.findByChannelIdAndDeletedFalseOrderByCreatedAtDesc(channelId, Pageable.unpaged()))
                .thenReturn(page);

        Page<Message> result = service.listChannelMessages(channelId, Pageable.unpaged());

        assertSame(page, result);
    }

    // --- getHistory() ---

    @Test
    void shouldReturnAuditLogs_whenGetHistoryAndMessageExists() {
        when(messageRepo.existsById(messageId)).thenReturn(true);
        MessageAuditLog log = new MessageAuditLog(messageId, AuditAction.CREATED, senderId, null, "hi");
        when(auditLogRepo.findByMessageIdOrderByCreatedAtAsc(messageId)).thenReturn(List.of(log));

        List<MessageAuditLog> result = service.getHistory(messageId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowResourceNotFoundException_whenGetHistoryAndMessageMissing() {
        when(messageRepo.existsById(messageId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.getHistory(messageId));
    }
}
