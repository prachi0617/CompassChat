package com.compasschat.ai.controller;

import com.compasschat.TestSecurityConfig;
import com.compasschat.ai.dto.ChatRequest;
import com.compasschat.ai.dto.ChatResponse;
import com.compasschat.ai.service.AiAgentService;
import com.compasschat.auth.security.JwtService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AiAgentController.class)
@Import(TestSecurityConfig.class)
class AiAgentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    @MockBean private AiAgentService aiAgentService;
    @MockBean private JwtService jwtService;

    private final UUID userId = UUID.randomUUID();

    @Test
    @WithMockUser
    void shouldReturn200WithChatResponse_whenPostValidMessage() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        ChatResponse chatResponse = new ChatResponse("Here to help!", "HOUSING", false);
        when(aiAgentService.processMessage(anyString(), any())).thenReturn(chatResponse);

        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new ChatRequest("I need housing help"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.response").value("Here to help!"));
    }
}
