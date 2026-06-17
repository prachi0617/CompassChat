package com.compasschat.channel;

import com.compasschat.common.base.BaseRepository;
import com.compasschat.common.enums.ChannelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends BaseRepository<Channel, UUID> {

    Optional<Channel> findByName(String name);

    boolean existsByName(String name);

    Page<Channel> findByTypeAndArchivedFalse(ChannelType type, Pageable pageable);
}
