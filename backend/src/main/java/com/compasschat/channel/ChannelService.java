package com.compasschat.channel;

import com.compasschat.channel.dto.CreateChannelRequest;
import com.compasschat.channel.dto.UpdateChannelRequest;
import com.compasschat.common.base.BaseService;
import com.compasschat.common.enums.ChannelType;
import com.compasschat.common.enums.Role;
import com.compasschat.common.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChannelService extends BaseService<Channel, UUID> {

    private final ChannelRepository channels;
    private final ChannelMemberRepository members;

    public ChannelService(ChannelRepository channels, ChannelMemberRepository members) {
        super(channels, "Channel");
        this.channels = channels;
        this.members = members;
    }

    // ----- CREATION -----

    @Transactional
    public Channel create(CreateChannelRequest req, UUID creatorId) {
        if (channels.existsByName(req.name())) {
            throw new IllegalStateException("Channel name '" + req.name() + "' is already taken");
        }

        Channel channel = new Channel(req.name(), req.description(), req.type());
        channel.setCreatedBy(creatorId);
        channel = channels.save(channel);

        members.save(new ChannelMember(channel.getId(), creatorId));

        return channel;
    }

    // ----- UPDATES -----

    @Transactional
    public Channel update(UUID channelId, UpdateChannelRequest req, UUID requesterId, Role role) {
        Channel ch = findById(channelId);
        requireChannelAdmin(ch, requesterId, role);

        if (req.name() != null && !req.name().isBlank() && !req.name().equals(ch.getName())) {
            if (channels.existsByName(req.name())) {
                throw new IllegalStateException("Channel name '" + req.name() + "' is already taken");
            }
            ch.rename(req.name());
        }
        if (req.description() != null) {
            ch.updateDescription(req.description());
        }
        return ch;
    }

    @Transactional
    public void archive(UUID channelId, UUID requesterId, Role role) {
        Channel ch = findById(channelId);
        requireChannelAdmin(ch, requesterId, role);
        ch.archive();
    }

    // ----- MEMBERSHIP -----

    @Transactional
    public ChannelMember addMember(UUID channelId, UUID userToAdd, UUID requesterId) {
        Channel ch = findById(channelId);
        if (ch.isArchived()) {
            throw new IllegalStateException("Cannot add members to an archived channel");
        }

        switch (ch.getType()) {
            case DIRECT -> throw new AccessDeniedException("Direct message channels are closed");
            case PRIVATE -> {
                if (!members.existsByChannelIdAndUserId(channelId, requesterId)) {
                    throw new AccessDeniedException("Only members can invite to a private channel");
                }
            }
            case PUBLIC -> {
                // Self-join is always fine; inviting someone else requires membership
                if (!userToAdd.equals(requesterId)
                        && !members.existsByChannelIdAndUserId(channelId, requesterId)) {
                    throw new AccessDeniedException("Join the channel before inviting others");
                }
            }
        }

        // Idempotent: already a member? return the existing row.
        return members.findByChannelIdAndUserId(channelId, userToAdd)
                .orElseGet(() -> members.save(new ChannelMember(channelId, userToAdd)));
    }

    @Transactional
    public void removeMember(UUID channelId, UUID userToRemove, UUID requesterId, Role role) {
        ChannelMember m = members.findByChannelIdAndUserId(channelId, userToRemove)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ChannelMember", channelId + "/" + userToRemove));

        // Self-leave is always allowed; otherwise need staff or channel admin.
        boolean isSelf = userToRemove.equals(requesterId);
        boolean isStaff = role == Role.ADMIN || role == Role.MODERATOR;
        Channel ch = findById(channelId);
        boolean isChannelCreator = requesterId.equals(ch.getCreatedBy());
        if (!isSelf && !isStaff && !isChannelCreator) {
            throw new AccessDeniedException("Not allowed to remove that member");
        }

        members.deleteById(m.getId());
    }

    // ----- READS -----

    @Transactional(readOnly = true)
    public List<ChannelMember> listMembers(UUID channelId) {
        if (!channels.existsById(channelId)) {
            throw new ResourceNotFoundException("Channel", channelId.toString());
        }
        return members.findByChannelId(channelId);
    }

    @Transactional(readOnly = true)
    public List<ChannelMember> listMyChannels(UUID userId) {
        return members.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getMemberCount(UUID channelId) {
        return members.countByChannelId(channelId);
    }

    /**
     * THE authorization gate other services use. Currently public
     * because MessageService needs to call it; in larger codebases
     * this would be its own AuthorizationService.
     */
    @Transactional(readOnly = true)
    public boolean canUserAccess(UUID channelId, UUID userId) {
        Channel ch = findById(channelId);
        if (ch.isArchived()) return false;
        if (ch.getType() == ChannelType.PUBLIC) return true;
        return members.existsByChannelIdAndUserId(channelId, userId);
    }

    // ----- helpers -----

    private void requireChannelAdmin(Channel ch, UUID requesterId, Role role) {
        boolean isCreator = requesterId.equals(ch.getCreatedBy());
        boolean isStaff = role == Role.ADMIN || role == Role.MODERATOR;
        if (!isCreator && !isStaff) {
            throw new AccessDeniedException("Not allowed to modify this channel");
        }
    }
}
