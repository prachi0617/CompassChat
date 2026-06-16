package com.compasschat.dm;

import com.compasschat.auth.security.JwtService;
import com.compasschat.common.base.ApiResponse;
import com.compasschat.dm.dto.DirectMessageResponse;
import com.compasschat.dm.dto.SendDirectMessageRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dm")
public class DirectMessageController {

    private final DirectMessageService directMessageService;
    private final JwtService jwtService;

    public DirectMessageController(DirectMessageService directMessageService, JwtService jwtService) {
        this.directMessageService = directMessageService;
        this.jwtService = jwtService;
    }

    /** Send a direct message to a user. */
    @PostMapping("/{recipientId}")
    public ResponseEntity<ApiResponse<DirectMessageResponse>> send(
            @PathVariable UUID recipientId,
            @Valid @RequestBody SendDirectMessageRequest req,
            HttpServletRequest http) {

        UUID senderId = currentUserId(http);
        DirectMessage dm = directMessageService.send(senderId, recipientId, req.content());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Message sent", DirectMessageResponse.from(dm)));
    }

    /** Get the conversation thread with a specific user, newest first. */
    @GetMapping("/{otherUserId}")
    public ApiResponse<Page<DirectMessageResponse>> conversation(
            @PathVariable UUID otherUserId,
            @PageableDefault(size = 50) Pageable pageable,
            HttpServletRequest http) {

        UUID requesterId = currentUserId(http);
        Page<DirectMessageResponse> page = directMessageService
                .getConversation(requesterId, otherUserId, pageable)
                .map(DirectMessageResponse::from);
        return ApiResponse.ok(page);
    }

    /** Soft-delete a direct message (sender only). */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id, HttpServletRequest http) {
        directMessageService.softDelete(id, currentUserId(http));
        return ApiResponse.ok("Message deleted", null);
    }

    // ----- JWT helpers -----

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
