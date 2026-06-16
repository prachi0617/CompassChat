package com.compasschat.volunteer;

import com.compasschat.TestSecurityConfig;
import com.compasschat.auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VolunteerController.class)
@Import(TestSecurityConfig.class)
class VolunteerControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private JwtService jwtService;

    @Test
    @WithMockUser
    void shouldReturn200WithStatusMessage_whenGetVolunteerStatus() throws Exception {
        mockMvc.perform(get("/api/volunteers/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("Volunteer module available"));
    }
}
