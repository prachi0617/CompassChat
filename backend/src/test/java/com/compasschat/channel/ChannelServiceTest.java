package com.compasschat.channel;

import com.compasschat.channel.dto.CreateChannelRequest;
import com.compasschat.channel.dto.UpdateChannelRequest;
import com.compasschat.common.base.exception.ResourceNotFoundException;
import com.compasschat.common.enums.ChannelType;
import com.compasschat.common.enums.Role;
import com.compasschat.message.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock private ChannelRepository channelRepo;
    @Mock private ChannelMemberRepository memberRepo;
    @Mock private MessageRepository messageRepo;

    private ChannelService service;

    private final UUID creatorId = UUID.randomUUID();
    private final UUID channelId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ChannelService(channelRepo, memberRepo, messageRepo);
    }

    private static void setId(Object entity, UUID id) throws Exception {
        Field f = entity.getClass().getSuperclass().getDeclaredField("id");
        f.setAccessible(true);
        f.set(entity, id);
    }

    private Channel channelWithId(String name, ChannelType type) throws Exception {
        Channel ch = new Channel(name, "desc", type);
        ch.setCreatedBy(creatorId);
        setId(ch, channelId);
        return ch;
    }

    // --- create() ---

    @Test
    void shouldCreateChannelAndAddCreatorAsMember_whenNameIsAvailable() {
        when(channelRepo.existsByName("general")).thenReturn(false);
        Channel saved = new Channel("general", "desc", ChannelType.PUBLIC);
        when(channelRepo.save(any(Channel.class))).thenReturn(saved);
        when(memberRepo.save(any(ChannelMember.class))).thenReturn(new ChannelMember(channelId, creatorId));

        Channel result = service.create(new CreateChannelRequest("general", "desc", ChannelType.PUBLIC), creatorId);

        assertNotNull(result);
        verify(channelRepo).save(any(Channel.class));
        verify(memberRepo).save(any(ChannelMember.class));
    }

    @Test
    void shouldThrowIllegalStateException_whenCreateWithDuplicateName() {
        when(channelRepo.existsByName("general")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> service.create(new CreateChannelRequest("general", "desc", ChannelType.PUBLIC), creatorId));
        verify(channelRepo, never()).save(any());
    }

    // --- update() ---

    @Test
    void shouldRenameChannel_whenCreatorUpdatesWithAvailableName() throws Exception {
        Channel ch = channelWithId("old-name", ChannelType.PUBLIC);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        when(channelRepo.existsByName("new-name")).thenReturn(false);

        service.update(channelId, new UpdateChannelRequest("new-name", null), creatorId, Role.MEMBER);

        assertEquals("new-name", ch.getName());
    }

    @Test
    void shouldThrowIllegalStateException_whenUpdateWithAlreadyTakenName() throws Exception {
        Channel ch = channelWithId("old-name", ChannelType.PUBLIC);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        when(channelRepo.existsByName("taken")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> service.update(channelId, new UpdateChannelRequest("taken", null), creatorId, Role.MEMBER));
    }

    @Test
    void shouldThrowAccessDeniedException_whenNonCreatorNonStaffUpdatesChannel() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        UUID stranger = UUID.randomUUID();

        assertThrows(AccessDeniedException.class,
                () -> service.update(channelId, new UpdateChannelRequest("new", null), stranger, Role.MEMBER));
    }

    @Test
    void shouldAllowUpdate_whenAdminUpdatesAnyChannel() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        UUID adminId = UUID.randomUUID();

        assertDoesNotThrow(
                () -> service.update(channelId, new UpdateChannelRequest(null, "new desc"), adminId, Role.ADMIN));
    }

    // --- archive() ---

    @Test
    void shouldArchiveChannel_whenCreatorArchives() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));

        service.archive(channelId, creatorId, Role.MEMBER);

        assertTrue(ch.isArchived());
    }

    @Test
    void shouldThrowAccessDeniedException_whenNonCreatorNonStaffArchives() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        UUID stranger = UUID.randomUUID();

        assertThrows(AccessDeniedException.class,
                () -> service.archive(channelId, stranger, Role.MEMBER));
    }

    // --- addMember() ---

    @Test
    void shouldAddMember_whenPublicChannelAndRequesterSelfJoins() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.empty());
        ChannelMember saved = new ChannelMember(channelId, userId);
        when(memberRepo.save(any(ChannelMember.class))).thenReturn(saved);

        ChannelMember result = service.addMember(channelId, userId, userId);

        assertNotNull(result);
        verify(memberRepo).save(any(ChannelMember.class));
    }

    @Test
    void shouldReturnExistingMember_whenUserAlreadyInChannel() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        ChannelMember existing = new ChannelMember(channelId, userId);
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.of(existing));

        ChannelMember result = service.addMember(channelId, userId, userId);

        assertSame(existing, result);
        verify(memberRepo, never()).save(any());
    }

    @Test
    void shouldThrowIllegalStateException_whenAddMemberToArchivedChannel() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        ch.archive();
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));

        assertThrows(IllegalStateException.class,
                () -> service.addMember(channelId, userId, userId));
    }

    @Test
    void shouldThrowAccessDeniedException_whenAddMemberToDirectChannel() throws Exception {
        Channel ch = channelWithId("dm", ChannelType.DIRECT);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));

        assertThrows(AccessDeniedException.class,
                () -> service.addMember(channelId, userId, userId));
    }

    @Test
    void shouldThrowAccessDeniedException_whenInvitingToPrivateChannelWithoutMembership() throws Exception {
        Channel ch = channelWithId("secret", ChannelType.PRIVATE);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        UUID invitee = UUID.randomUUID();
        when(memberRepo.existsByChannelIdAndUserId(channelId, userId)).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> service.addMember(channelId, invitee, userId));
    }

    // --- removeMember() ---

    @Test
    void shouldRemoveMember_whenUserLeavesThemselves() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        ChannelMember member = new ChannelMember(channelId, userId);
        setId(member, UUID.randomUUID());
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.of(member));
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));

        assertDoesNotThrow(() -> service.removeMember(channelId, userId, userId, Role.MEMBER));

        verify(memberRepo).deleteById(member.getId());
    }

    @Test
    void shouldRemoveMember_whenAdminRemovesOtherUser() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        UUID adminId = UUID.randomUUID();
        ChannelMember member = new ChannelMember(channelId, userId);
        setId(member, UUID.randomUUID());
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.of(member));
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));

        assertDoesNotThrow(() -> service.removeMember(channelId, userId, adminId, Role.ADMIN));

        verify(memberRepo).deleteById(member.getId());
    }

    @Test
    void shouldThrowAccessDeniedException_whenNonStaffRemovesOtherUser() throws Exception {
        UUID randomId = UUID.randomUUID();
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        ChannelMember member = new ChannelMember(channelId, userId);
        setId(member, UUID.randomUUID());
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.of(member));
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));

        assertThrows(AccessDeniedException.class,
                () -> service.removeMember(channelId, userId, randomId, Role.MEMBER));
    }

    @Test
    void shouldThrowResourceNotFoundException_whenRemoveMemberWhoIsNotInChannel() {
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.removeMember(channelId, userId, creatorId, Role.ADMIN));
    }

    // --- canUserAccess() ---

    @Test
    void shouldReturnTrue_whenPublicChannelAndNotArchived() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));

        assertTrue(service.canUserAccess(channelId, userId));
    }

    @Test
    void shouldReturnFalse_whenChannelIsArchived() throws Exception {
        Channel ch = channelWithId("general", ChannelType.PUBLIC);
        ch.archive();
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));

        assertFalse(service.canUserAccess(channelId, userId));
    }

    @Test
    void shouldReturnTrue_whenPrivateChannelAndUserIsMember() throws Exception {
        Channel ch = channelWithId("secret", ChannelType.PRIVATE);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        when(memberRepo.existsByChannelIdAndUserId(channelId, userId)).thenReturn(true);

        assertTrue(service.canUserAccess(channelId, userId));
    }

    @Test
    void shouldReturnFalse_whenPrivateChannelAndUserIsNotMember() throws Exception {
        Channel ch = channelWithId("secret", ChannelType.PRIVATE);
        when(channelRepo.findById(channelId)).thenReturn(Optional.of(ch));
        when(memberRepo.existsByChannelIdAndUserId(channelId, userId)).thenReturn(false);

        assertFalse(service.canUserAccess(channelId, userId));
    }

    // --- listMembers() ---

    @Test
    void shouldReturnMemberList_whenChannelExists() throws Exception {
        when(channelRepo.existsById(channelId)).thenReturn(true);
        ChannelMember m = new ChannelMember(channelId, userId);
        when(memberRepo.findByChannelId(channelId)).thenReturn(List.of(m));

        List<ChannelMember> result = service.listMembers(channelId);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowResourceNotFoundException_whenListMembersForNonExistentChannel() {
        when(channelRepo.existsById(channelId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.listMembers(channelId));
    }

    // --- getUnreadCount() ---

    @Test
    void shouldReturnZero_whenUserIsNotMemberOfChannel() {
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.empty());

        long count = service.getUnreadCount(channelId, userId);

        assertEquals(0L, count);
    }

    @Test
    void shouldReturnCount_whenUserHasLastReadAt() throws Exception {
        ChannelMember member = new ChannelMember(channelId, userId);
        member.markRead(); // sets lastReadAt to now
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.of(member));
        when(messageRepo.countByChannelIdAndCreatedAtAfterAndDeletedFalse(eq(channelId), any(LocalDateTime.class)))
                .thenReturn(5L);

        long count = service.getUnreadCount(channelId, userId);

        assertEquals(5L, count);
    }

    // --- markRead() ---

    @Test
    void shouldUpdateLastReadAt_whenMarkReadAndMemberExists() {
        ChannelMember member = new ChannelMember(channelId, userId);
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.of(member));
        when(memberRepo.save(member)).thenReturn(member);

        service.markRead(channelId, userId);

        assertNotNull(member.getLastReadAt());
        verify(memberRepo).save(member);
    }

    @Test
    void shouldDoNothing_whenMarkReadAndMemberNotFound() {
        when(memberRepo.findByChannelIdAndUserId(channelId, userId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.markRead(channelId, userId));
        verify(memberRepo, never()).save(any());
    }

    // --- listAccessible() ---

    @Test
    void shouldReturnPublicChannels_whenUserHasNoMemberships() throws Exception {
        when(memberRepo.findByUserId(userId)).thenReturn(List.of());
        Channel pub = channelWithId("general", ChannelType.PUBLIC);
        when(channelRepo.findByTypeAndArchivedFalse(eq(ChannelType.PUBLIC), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(pub)));
        when(memberRepo.countByChannelId(channelId)).thenReturn(0L);

        var result = service.listAccessible(userId);

        assertEquals(1, result.size());
    }
}
