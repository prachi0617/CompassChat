package com.compasschat.ai.controller;

import com.compasschat.ai.dto.ChatRequest;
import com.compasschat.ai.dto.ChatResponse;
import com.compasschat.ai.dto.EscalateRequest;
import com.compasschat.ai.dto.EscalateResponse;
import com.compasschat.ai.escalation.EscalationService;
import com.compasschat.ai.service.AiAgentService;
import com.compasschat.auth.security.JwtService;
import com.compasschat.common.base.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
public class AiAgentController {

    private final AiAgentService aiAgentService;
    private final EscalationService escalationService;
    private final JwtService jwtService;

    public AiAgentController(AiAgentService aiAgentService,
                             EscalationService escalationService,
                             JwtService jwtService) {
        this.aiAgentService = aiAgentService;
        this.escalationService = escalationService;
        this.jwtService = jwtService;
    }

    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(@RequestBody ChatRequest request,
                                          HttpServletRequest http) {
        UUID userId = extractOptionalUserId(http);
        return ApiResponse.ok(aiAgentService.processMessage(request.message(), userId, request.history()));
    }

    @PostMapping("/escalate")
    public ApiResponse<EscalateResponse> escalate(@RequestBody EscalateRequest request,
                                                   HttpServletRequest http) {
        UUID userId = extractOptionalUserId(http);
        return ApiResponse.ok(escalationService.escalate(userId, request.contextMessage()));
    }

    private UUID extractOptionalUserId(HttpServletRequest http) {
        try {
            String header = http.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) return null;
            return jwtService.getUserId(header.substring(7));
        } catch (Exception e) {
            return null;
        }
    }
}
