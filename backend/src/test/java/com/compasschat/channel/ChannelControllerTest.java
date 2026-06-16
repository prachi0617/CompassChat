package com.compasschat.channel;

import com.compasschat.TestSecurityConfig;
import com.compasschat.auth.security.JwtService;
import com.compasschat.channel.dto.*;
import com.compasschat.common.enums.ChannelType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChannelController.class)
@Import(TestSecurityConfig.class)
class ChannelControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    @MockBean private ChannelService channelService;
    @MockBean private JwtService jwtService;

    private final UUID channelId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID memberId = UUID.randomUUID();

    private Channel publicChannel() {
        Channel ch = new Channel("general", "A channel", ChannelType.PUBLIC);
        ch.setCreatedBy(userId);
        return ch;
    }

    @Test
    @WithMockUser
    void shouldReturn201WithChannel_whenCreateValidChannel() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        Channel ch = publicChannel();
        when(channelService.create(any(), eq(userId))).thenReturn(ch);
        when(channelService.getMemberCount(any())).thenReturn(1L);

        mockMvc.perform(post("/api/channels")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                new CreateChannelRequest("general", "A channel", ChannelType.PUBLIC))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("general"));
    }

    @Test
    @WithMockUser
    void shouldReturn400_whenCreateWithInvalidName() throws Exception {
        mockMvc.perform(post("/api/channels")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(
                                new CreateChannelRequest("Has Spaces!", "desc", ChannelType.PUBLIC))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldReturn200WithChannel_whenGetById() throws Exception {
        Channel ch = publicChannel();
        when(channelService.findById(channelId)).thenReturn(ch);
        when(channelService.getMemberCount(channelId)).thenReturn(3L);

        mockMvc.perform(get("/api/channels/{id}", channelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("general"));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithUpdatedChannel_whenUpdateChannel() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        when(jwtService.getRole(any())).thenReturn("MEMBER");
        Channel ch = publicChannel();
        when(channelService.update(eq(channelId), any(), eq(userId), any())).thenReturn(ch);
        when(channelService.getMemberCount(channelId)).thenReturn(1L);

        mockMvc.perform(put("/api/channels/{id}", channelId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new UpdateChannelRequest("general", "updated"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void shouldReturn200_whenArchiveChannel() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        when(jwtService.getRole(any())).thenReturn("MEMBER");

        mockMvc.perform(post("/api/channels/{id}/archive", channelId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(channelService).archive(eq(channelId), eq(userId), any());
    }

    @Test
    @WithMockUser
    void shouldReturn200WithChannelList_whenListAccessible() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        when(channelService.listAccessible(userId)).thenReturn(List.of());

        mockMvc.perform(get("/api/channels")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser
    void shouldReturn200_whenMarkRead() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);

        mockMvc.perform(post("/api/channels/{id}/read", channelId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());

        verify(channelService).markRead(channelId, userId);
    }

    @Test
    @WithMockUser
    void shouldReturn200WithUnreadCount_whenGetUnreadCount() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        when(channelService.getUnreadCount(channelId, userId)).thenReturn(5L);

        mockMvc.perform(get("/api/channels/{id}/unread", channelId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithMemberList_whenListMyChannels() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        when(channelService.listMyChannels(userId)).thenReturn(List.of());

        mockMvc.perform(get("/api/channels/mine")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser
    void shouldReturn200WithMemberList_whenGetMembers() throws Exception {
        when(channelService.listMembers(channelId)).thenReturn(List.of());

        mockMvc.perform(get("/api/channels/{id}/members", channelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser
    void shouldReturn201WithMember_whenAddMember() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        ChannelMember member = new ChannelMember(channelId, memberId);
        when(channelService.addMember(eq(channelId), eq(memberId), eq(userId))).thenReturn(member);

        mockMvc.perform(post("/api/channels/{id}/members", channelId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new AddMemberRequest(memberId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void shouldReturn200_whenRemoveMember() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        when(jwtService.getRole(any())).thenReturn("ADMIN");

        mockMvc.perform(delete("/api/channels/{id}/members/{userId}", channelId, memberId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());

        verify(channelService).removeMember(eq(channelId), eq(memberId), eq(userId), any());
    }
}
