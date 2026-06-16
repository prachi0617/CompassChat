package com.compasschat.channel;

import com.compasschat.channel.dto.ChannelMemberResponse;
import com.compasschat.channel.dto.ChannelResponse;
import com.compasschat.common.enums.ChannelType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChannelDtoTest {

    @Test
    void shouldMapChannelToChannelResponse_whenFromCalled() {
        Channel ch = new Channel("general", "A public channel", ChannelType.PUBLIC);
        ChannelResponse response = ChannelResponse.from(ch, 5L);

        assertEquals("general", response.name());
        assertEquals("A public channel", response.description());
        assertEquals(ChannelType.PUBLIC, response.type());
        assertEquals(5L, response.memberCount());
        assertFalse(response.archived());
    }

    @Test
    void shouldMapChannelMemberToResponse_whenFromCalled() {
        UUID channelId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ChannelMember member = new ChannelMember(channelId, userId);

        ChannelMemberResponse response = ChannelMemberResponse.from(member);

        assertEquals(channelId, response.channelId());
        assertEquals(userId, response.userId());
        assertFalse(response.muted());
    }
}
