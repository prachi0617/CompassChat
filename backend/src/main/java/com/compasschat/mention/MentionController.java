package com.compasschat.mention;

import com.compasschat.auth.security.JwtService;
import com.compasschat.common.base.ApiResponse;
import com.compasschat.mention.dto.MentionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/mentions")
public class MentionController {

    private final MentionService mentionService;
    private final JwtService jwtService;

    public MentionController(MentionService mentionService, JwtService jwtService) {
        this.mentionService = mentionService;
        this.jwtService = jwtService;
    }

    /** All mentions for the current user (read + unread). */
    @GetMapping("/me")
    public ApiResponse<List<MentionResponse>> all(HttpServletRequest http) {
        return ApiResponse.ok(mentionService.getAll(currentUserId(http)));
    }

    /** Unread-only mentions for the current user. */
    @GetMapping("/me/unread")
    public ApiResponse<List<MentionResponse>> unread(HttpServletRequest http) {
        return ApiResponse.ok(mentionService.getUnread(currentUserId(http)));
    }

    /** Unread mention count badge — lightweight poll for the frontend. */
    @GetMapping("/me/unread/count")
    public ApiResponse<Long> unreadCount(HttpServletRequest http) {
        return ApiResponse.ok(mentionService.countUnread(currentUserId(http)));
    }

    /** Mark a single mention as read. */
    @PostMapping("/{id}/read")
    public ApiResponse<MentionResponse> markRead(@PathVariable UUID id, HttpServletRequest http) {
        return ApiResponse.ok(mentionService.markRead(id, currentUserId(http)));
    }

    private UUID currentUserId(HttpServletRequest http) {
        return jwtService.getUserId(extractToken(http));
    }

    private String extractToken(HttpServletRequest http) {
        String header = http.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing token");
        }
        return header.substring(7);
    }
}
