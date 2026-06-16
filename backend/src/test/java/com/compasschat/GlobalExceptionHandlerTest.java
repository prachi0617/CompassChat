package com.compasschat;

import com.compasschat.auth.security.JwtService;
import com.compasschat.channel.Channel;
import com.compasschat.channel.ChannelController;
import com.compasschat.channel.ChannelService;
import com.compasschat.channel.dto.CreateChannelRequest;
import com.compasschat.common.base.exception.ResourceNotFoundException;
import com.compasschat.common.enums.ChannelType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Exercises GlobalExceptionHandler via ChannelController — no separate controller needed.
 */
@WebMvcTest(ChannelController.class)
@Import(TestSecurityConfig.class)
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    @MockBean private ChannelService channelService;
    @MockBean private JwtService jwtService;

    @Test
    @WithMockUser
    void shouldReturn400WithFieldError_whenChannelNameFailsPatternValidation() throws Exception {
        CreateChannelRequest invalid = new CreateChannelRequest("Has Spaces!", "desc", ChannelType.PUBLIC);

        mockMvc.perform(post("/api/channels")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").isString());
    }

    @Test
    @WithMockUser
    void shouldReturn404WithErrorMessage_whenResourceNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(channelService.findById(id))
                .thenThrow(new ResourceNotFoundException("Channel", id.toString()));

        mockMvc.perform(get("/api/channels/{id}", id)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser
    void shouldReturn500_whenUnexpectedExceptionThrownFromService() throws Exception {
        UUID creatorId = UUID.randomUUID();
        when(jwtService.getUserId(any())).thenReturn(creatorId);
        when(jwtService.getRole(any())).thenReturn("MEMBER");
        when(channelService.create(any(CreateChannelRequest.class), eq(creatorId)))
                .thenThrow(new RuntimeException("Unexpected database error"));

        CreateChannelRequest req = new CreateChannelRequest("valid-name", "desc", ChannelType.PUBLIC);

        mockMvc.perform(post("/api/channels")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Something went wrong on our side"));
    }
}
