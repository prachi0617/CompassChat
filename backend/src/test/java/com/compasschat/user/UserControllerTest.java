package com.compasschat.user;

import com.compasschat.TestSecurityConfig;
import com.compasschat.auth.security.JwtService;
import com.compasschat.common.enums.PresenceStatus;
import com.compasschat.common.enums.Role;
import com.compasschat.user.dto.UpdateUserRequest;
import com.compasschat.websocket.PresenceService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper json;

    @MockBean private UserService userService;
    @MockBean private JwtService jwtService;
    @MockBean private PresenceService presenceService;

    private final UUID userId = UUID.randomUUID();

    @Test
    @WithMockUser
    void shouldReturn200WithCurrentUser_whenGetMe() throws Exception {
        User user = new User("alice", "hash", Role.MEMBER);
        when(jwtService.getUsername(any())).thenReturn("alice");
        when(userService.findByUsername("alice")).thenReturn(user);

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    @WithMockUser
    void shouldReturn200_whenUpdatePresence() throws Exception {
        when(jwtService.getUserId(any())).thenReturn(userId);

        mockMvc.perform(put("/api/users/me/presence")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ONLINE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(presenceService).setStatus(eq(userId), eq(PresenceStatus.ONLINE));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithUser_whenGetUserById() throws Exception {
        User user = new User("bob", "hash", Role.MEMBER);
        when(userService.findById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("bob"));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithUserList_whenGetAllUsers() throws Exception {
        User user = new User("charlie", "hash", Role.MEMBER);
        when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("charlie"));
    }

    @Test
    @WithMockUser
    void shouldReturn200WithUpdatedUser_whenUpdateUser() throws Exception {
        User user = new User("alice", "hash", Role.MEMBER);
        user.setEmail("new@email.com");
        when(userService.updateProfile(eq(userId), any(UpdateUserRequest.class))).thenReturn(user);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(new UpdateUserRequest(null, "new@email.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("new@email.com"));
    }
}
