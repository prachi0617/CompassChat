package com.compasschat.mention;

import com.compasschat.common.base.exception.ResourceNotFoundException;
import com.compasschat.mention.dto.MentionResponse;
import com.compasschat.user.User;
import com.compasschat.user.UserRepository;
import com.compasschat.common.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentionServiceTest {

    @Mock private MentionRepository mentionRepo;
    @Mock private UserRepository userRepo;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private SimpMessagingTemplate broker;

    private MentionService service;

    private final UUID messageId = UUID.randomUUID();
    private final UUID channelId = UUID.randomUUID();
    private final UUID senderUserId = UUID.randomUUID();
    private final UUID mentionedUserId = UUID.randomUUID();
    private final UUID mentionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new MentionService(mentionRepo, userRepo, eventPublisher, broker);
    }

    /** Reflectively set AuditableEntity.id — there is no public setter. */
    private static void setId(Object entity, UUID id) throws Exception {
        Field f = entity.getClass().getSuperclass().getDeclaredField("id");
        f.setAccessible(true);
        f.set(entity, id);
    }

    // --- extractAndSave() ---

    @Test
    void shouldSaveMentionAndPublishEvent_whenContentMentionsKnownUser() throws Exception {
        User mentionedUser = new User("alice", "hash", Role.MEMBER);
        setId(mentionedUser, mentionedUserId);
        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(mentionedUser));
        Mention saved = new Mention(messageId, channelId, senderUserId, mentionedUserId);
        when(mentionRepo.save(any(Mention.class))).thenReturn(saved);

        service.extractAndSave(messageId, channelId, senderUserId, "Hello @alice!");

        verify(mentionRepo).save(any(Mention.class));
        verify(eventPublisher).publishEvent(any(UserMentionedEvent.class));
        verify(broker).convertAndSendToUser(eq("alice"), eq("/queue/mentions"), any(MentionResponse.class));
    }

    @Test
    void shouldSkipMention_whenMentionedUsernameIsUnknown() {
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        service.extractAndSave(messageId, channelId, senderUserId, "Hey @ghost!");

        verify(mentionRepo, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldSkipMention_whenSenderMentionsThemselves() throws Exception {
        User sender = new User("bob", "hash", Role.MEMBER);
        setId(sender, senderUserId); // same ID as senderUserId — triggers self-mention guard
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(sender));

        service.extractAndSave(messageId, channelId, senderUserId, "I did it @bob!");

        verify(mentionRepo, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void shouldProcessMultipleMentions_whenContentHasTwoDistinctMentions() throws Exception {
        User alice = new User("alice", "hash", Role.MEMBER);
        setId(alice, mentionedUserId);
        UUID bobId = UUID.randomUUID();
        User bob = new User("bob", "hash", Role.MEMBER);
        setId(bob, bobId);

        when(userRepo.findByUsername("alice")).thenReturn(Optional.of(alice));
        when(userRepo.findByUsername("bob")).thenReturn(Optional.of(bob));

        Mention m1 = new Mention(messageId, channelId, senderUserId, mentionedUserId);
        Mention m2 = new Mention(messageId, channelId, senderUserId, bobId);
        when(mentionRepo.save(any(Mention.class))).thenReturn(m1, m2);

        service.extractAndSave(messageId, channelId, senderUserId, "@alice and @bob check this");

        verify(mentionRepo, times(2)).save(any(Mention.class));
        verify(eventPublisher, times(2)).publishEvent(any(UserMentionedEvent.class));
    }

    @Test
    void shouldDoNothing_whenContentHasNoMentions() {
        service.extractAndSave(messageId, channelId, senderUserId, "No mentions here.");

        verifyNoInteractions(userRepo, mentionRepo, eventPublisher, broker);
    }

    // --- getUnread() ---

    @Test
    void shouldReturnUnreadMentions_whenUnreadMentionsExist() {
        Mention mention = new Mention(messageId, channelId, senderUserId, mentionedUserId);
        when(mentionRepo.findByMentionedUserIdAndReadFalseOrderByCreatedAtDesc(mentionedUserId))
                .thenReturn(List.of(mention));

        List<MentionResponse> result = service.getUnread(mentionedUserId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyList_whenNoUnreadMentions() {
        when(mentionRepo.findByMentionedUserIdAndReadFalseOrderByCreatedAtDesc(mentionedUserId))
                .thenReturn(List.of());

        List<MentionResponse> result = service.getUnread(mentionedUserId);

        assertTrue(result.isEmpty());
    }

    // --- getAll() ---

    @Test
    void shouldReturnAllMentions_whenMentionsExist() {
        Mention mention = new Mention(messageId, channelId, senderUserId, mentionedUserId);
        when(mentionRepo.findByMentionedUserIdOrderByCreatedAtDesc(mentionedUserId))
                .thenReturn(List.of(mention));

        List<MentionResponse> result = service.getAll(mentionedUserId);

        assertEquals(1, result.size());
    }

    // --- countUnread() ---

    @Test
    void shouldReturnUnreadCount_whenUnreadMentionsExist() {
        when(mentionRepo.countByMentionedUserIdAndReadFalse(mentionedUserId)).thenReturn(3L);

        long count = service.countUnread(mentionedUserId);

        assertEquals(3L, count);
    }

    // --- markRead() ---

    @Test
    void shouldMarkMentionAsRead_whenRequesterIsOwner() throws Exception {
        Mention mention = new Mention(messageId, channelId, senderUserId, mentionedUserId);
        setId(mention, mentionId);
        when(mentionRepo.findById(mentionId)).thenReturn(Optional.of(mention));
        when(mentionRepo.save(mention)).thenReturn(mention);

        MentionResponse response = service.markRead(mentionId, mentionedUserId);

        assertTrue(mention.isRead());
        verify(mentionRepo).save(mention);
        assertNotNull(response);
    }

    @Test
    void shouldThrowAccessDeniedException_whenMarkReadByDifferentUser() throws Exception {
        UUID otherId = UUID.randomUUID();
        Mention mention = new Mention(messageId, channelId, senderUserId, mentionedUserId);
        setId(mention, mentionId);
        when(mentionRepo.findById(mentionId)).thenReturn(Optional.of(mention));

        assertThrows(AccessDeniedException.class, () -> service.markRead(mentionId, otherId));
        verify(mentionRepo, never()).save(any());
    }

    @Test
    void shouldThrowResourceNotFoundException_whenMarkReadAndMentionMissing() {
        when(mentionRepo.findById(mentionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.markRead(mentionId, mentionedUserId));
    }
}
