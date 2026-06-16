package com.compasschat.mood;

import com.compasschat.TestSecurityConfig;
import com.compasschat.auth.security.JwtService;
import com.compasschat.common.enums.MoodType;
import com.compasschat.mood.dto.CreateMoodRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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

@WebMvcTest(MoodController.class)
@Import(TestSecurityConfig.class)
class MoodControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    @MockBean private MoodService moodService;
    @MockBean private JwtService jwtService;

    private final UUID userId = UUID.randomUUID();
    private final UUID moodId = UUID.randomUUID();

    @Test
    @WithMockUser
    void shouldReturn201_whenLogMoodWithValidRequest() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        Mood mood = new Mood(userId, MoodType.HAPPY, "great day");
        MoodService.MoodLogResult result = new MoodService.MoodLogResult(mood, List.of());
        when(moodService.logMood(eq(userId), eq(MoodType.HAPPY), eq("great day"))).thenReturn(result);

        mockMvc.perform(post("/api/moods")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new CreateMoodRequest(MoodType.HAPPY, "great day"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithHistory_whenGetMyHistory() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        Mood mood = new Mood(userId, MoodType.CALM, "ok");
        when(moodService.getHistoryForUser(eq(userId), eq(userId), any()))
                .thenReturn(new PageImpl<>(List.of(mood)));

        mockMvc.perform(get("/api/moods/me")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithMood_whenGetOneById() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);
        Mood mood = new Mood(userId, MoodType.ANXIOUS, "worried");
        when(moodService.getOneForUser(eq(moodId), eq(userId))).thenReturn(mood);

        mockMvc.perform(get("/api/moods/{id}", moodId)
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
