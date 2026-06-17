package com.compasschat;

import com.compasschat.auth.security.JwtService;
import com.compasschat.common.enums.Role;
import com.compasschat.message.Message;
import com.compasschat.message.MessageAuditLog;
import com.compasschat.message.MessageController;
import com.compasschat.message.MessageService;
import com.compasschat.message.dto.CreateMessageRequest;
import com.compasschat.message.dto.UpdateMessageRequest;
import com.compasschat.common.enums.AuditAction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@Import(TestSecurityConfig.class)
class MessageControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    @MockBean private MessageService messageService;
    @MockBean private JwtService jwtService;

    private final UUID channelId = UUID.randomUUID();
    private final UUID messageId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    @WithMockUser
    void shouldReturn201WithMessageContent_whenPostValidMessage() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        Message saved = new Message(userId, channelId, "Hello team");
        when(messageService.post(eq(channelId), eq(userId), eq("Hello team"))).thenReturn(saved);

        mockMvc.perform(post("/api/channels/{id}/messages", channelId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new CreateMessageRequest("Hello team"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Hello team"));
    }

    @Test
    @WithMockUser
    void shouldReturn400_whenPostWithBlankContent() throws Exception {
        mockMvc.perform(post("/api/channels/{id}/messages", channelId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new CreateMessageRequest(""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldReturn200WithPage_whenListMessages() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        Message msg = new Message(userId, channelId, "hi");
        Page<Message> page = new PageImpl<>(List.of(msg));
        when(messageService.listChannelMessages(eq(channelId), any())).thenReturn(page);

        mockMvc.perform(get("/api/channels/{id}/messages", channelId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithUpdatedMessage_whenEditValidMessage() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        Message updated = new Message(userId, channelId, "edited content");
        when(messageService.edit(eq(messageId), eq(userId), eq("edited content"))).thenReturn(updated);

        mockMvc.perform(put("/api/messages/{id}", messageId)
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new UpdateMessageRequest("edited content"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("edited content"));
    }

    @Test
    @WithMockUser
    void shouldReturn200_whenDeleteMessage() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        when(jwtService.getRole(any())).thenReturn(Role.MEMBER.name());

        mockMvc.perform(delete("/api/messages/{id}", messageId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithHistoryList_whenGetHistory() throws Exception {
        MessageAuditLog log = new MessageAuditLog(messageId, AuditAction.CREATED, userId, null, "Hello");
        when(messageService.getHistory(messageId)).thenReturn(List.of(log));

        mockMvc.perform(get("/api/messages/{id}/history", messageId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].action").value("CREATED"));
    }

    @Test
    @WithMockUser
    void shouldReturn200EmptyList_whenGetHistoryAndNoLogs() throws Exception {
        when(messageService.getHistory(messageId)).thenReturn(List.of());

        mockMvc.perform(get("/api/messages/{id}/history", messageId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}
