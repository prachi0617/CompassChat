package com.compasschat.channel;

import com.compasschat.common.base.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelMemberRepository extends BaseRepository<ChannelMember, UUID> {

    Optional<ChannelMember> findByChannelIdAndUserId(UUID channelId, UUID userId);

    boolean existsByChannelIdAndUserId(UUID channelId, UUID userId);

    List<ChannelMember> findByChannelId(UUID channelId);

    List<ChannelMember> findByUserId(UUID userId);

    long countByChannelId(UUID channelId);
}
