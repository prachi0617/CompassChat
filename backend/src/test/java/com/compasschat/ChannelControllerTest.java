package com.compasschat;

import com.compasschat.auth.security.JwtService;
import com.compasschat.channel.Channel;
import com.compasschat.channel.ChannelController;
import com.compasschat.channel.ChannelService;
import com.compasschat.channel.dto.CreateChannelRequest;
import com.compasschat.common.enums.ChannelType;
import com.compasschat.common.enums.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @WebMvcTest spins up just the web layer + ChannelController — no database, no real services.
 * TestSecurityConfig replaces SecurityConfig + JwtAuthFilter so @WithMockUser owns auth.
 */
@WebMvcTest(ChannelController.class)
@Import(TestSecurityConfig.class)
class ChannelControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    @MockBean private ChannelService channelService;
    @MockBean private JwtService jwtService;

    @Test
    @WithMockUser
    void shouldReturn201AndCreatedChannel_whenValidRequest() throws Exception {
        UUID creatorId = UUID.randomUUID();
        Channel created = new Channel("general", "Main channel", ChannelType.PUBLIC);

        when(jwtService.getUserId(any())).thenReturn(creatorId);
        when(jwtService.getRole(any())).thenReturn(Role.MEMBER.name());
        when(channelService.create(any(CreateChannelRequest.class), eq(creatorId)))
                .thenReturn(created);
        when(channelService.getMemberCount(any())).thenReturn(1L);

        CreateChannelRequest body = new CreateChannelRequest(
                "general", "Main channel", ChannelType.PUBLIC);

        mockMvc.perform(post("/api/channels")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("general"))
                .andExpect(jsonPath("$.data.type").value("PUBLIC"));
    }

    @Test
    @WithMockUser
    void shouldReturn400_whenChannelNameInvalid() throws Exception {
        // @Pattern("^[a-z0-9_-]+$") fires before the service is called
        CreateChannelRequest invalid = new CreateChannelRequest(
                "Has Spaces!", "x", ChannelType.PUBLIC);

        mockMvc.perform(post("/api/channels")
                        .header("Authorization", "Bearer fake-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
