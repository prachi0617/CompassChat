package com.compasschat.ai.controller;

import com.compasschat.ai.dto.ChatRequest;
import com.compasschat.ai.dto.ChatResponse;
import com.compasschat.ai.service.AiAgentService;

import com.compasschat.common.base.ApiResponse;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiAgentController {

    private final AiAgentService aiAgentService;

    public AiAgentController(
            AiAgentService aiAgentService) {

        this.aiAgentService = aiAgentService;
    }

    @PostMapping("/chat")
    public ApiResponse<ChatResponse> chat(
            @RequestBody ChatRequest request) {

        ChatResponse response =
                aiAgentService.processMessage(
                        request.message()
                );

        return ApiResponse.ok(response);
    }
}
