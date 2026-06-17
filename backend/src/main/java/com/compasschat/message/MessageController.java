package com.compasschat.message;

import com.compasschat.auth.security.JwtService;
import com.compasschat.common.base.ApiResponse;
import com.compasschat.common.enums.Role;
import com.compasschat.message.dto.CreateMessageRequest;
import com.compasschat.message.dto.MessageAuditLogResponse;
import com.compasschat.message.dto.MessageResponse;
import com.compasschat.message.dto.UpdateMessageRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;
    private final JwtService jwtService;

    public MessageController(MessageService messageService, JwtService jwtService) {
        this.messageService = messageService;
        this.jwtService = jwtService;
    }

    @PostMapping("/channels/{channelId}/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> post(
            @PathVariable UUID channelId,
            @Valid @RequestBody CreateMessageRequest req,
            HttpServletRequest http) {

        UUID senderId = currentUserId(http);
        Message saved = messageService.post(channelId, senderId, req.content());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Message posted", MessageResponse.from(saved)));
    }

    @GetMapping("/channels/{channelId}/messages")
    public ApiResponse<Page<MessageResponse>> list(
            @PathVariable UUID channelId,
            Pageable pageable) {

        Page<MessageResponse> page = messageService
                .listChannelMessages(channelId, pageable)
                .map(MessageResponse::from);
        return ApiResponse.ok(page);
    }

    @PutMapping("/messages/{id}")
    public ApiResponse<MessageResponse> edit(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMessageRequest req,
            HttpServletRequest http) {

        UUID requesterId = currentUserId(http);
        Message updated = messageService.edit(id, requesterId, req.content());
        return ApiResponse.ok("Message edited", MessageResponse.from(updated));
    }

    @DeleteMapping("/messages/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id, HttpServletRequest http) {
        UUID requesterId = currentUserId(http);
        Role role = currentRole(http);
        messageService.softDelete(id, requesterId, role);
        return ApiResponse.ok("Message deleted", null);
    }

    @GetMapping("/messages/{id}/history")
    public ApiResponse<List<MessageAuditLogResponse>> history(@PathVariable UUID id) {
        // (You'd protect this with @PreAuthorize("hasRole('MODERATOR')") in
        // a hardened version. For demo it's open.)
        List<MessageAuditLogResponse> entries = messageService.getHistory(id).stream()
                .map(MessageAuditLogResponse::from)
                .toList();
        return ApiResponse.ok(entries);
    }

    // ----- helpers for pulling identity off the JWT -----

    private UUID currentUserId(HttpServletRequest http) {
        return jwtService.getUserId(extractToken(http));
    }

    private Role currentRole(HttpServletRequest http) {
        return Role.valueOf(jwtService.getRole(extractToken(http)));
    }

    private String extractToken(HttpServletRequest http) {
        String header = http.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new org.springframework.security.access.AccessDeniedException("Missing token");
        }
        return header.substring(7);
    }
}

