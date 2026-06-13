package com.compasschat.channel;

import com.compasschat.auth.JwtService;
import com.compasschat.channel.dto.*;
import com.compasschat.common.base.ApiResponse;
import com.compasschat.common.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;
    private final JwtService jwtService;

    public ChannelController(ChannelService channelService, JwtService jwtService) {
        this.channelService = channelService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChannelResponse>> create(
            @Valid @RequestBody CreateChannelRequest req,
            HttpServletRequest http) {

        UUID creatorId = currentUserId(http);
        Channel ch = channelService.create(req, creatorId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Channel created",
                        ChannelResponse.from(ch, channelService.getMemberCount(ch.getId()))));
    }

    @GetMapping("/{id}")
    public ApiResponse<ChannelResponse> get(@PathVariable UUID id) {
        Channel ch = channelService.findById(id);
        return ApiResponse.ok(ChannelResponse.from(ch, channelService.getMemberCount(id)));
    }

    @PutMapping("/{id}")
    public ApiResponse<ChannelResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateChannelRequest req,
            HttpServletRequest http) {

        Channel ch = channelService.update(id, req, currentUserId(http), currentRole(http));
        return ApiResponse.ok("Channel updated",
                ChannelResponse.from(ch, channelService.getMemberCount(id)));
    }

    @PostMapping("/{id}/archive")
    public ApiResponse<Void> archive(@PathVariable UUID id, HttpServletRequest http) {
        channelService.archive(id, currentUserId(http), currentRole(http));
        return ApiResponse.ok("Channel archived", null);
    }

    @GetMapping("/mine")
    public ApiResponse<List<ChannelMemberResponse>> listMine(HttpServletRequest http) {
        List<ChannelMemberResponse> mine = channelService.listMyChannels(currentUserId(http))
                .stream()
                .map(ChannelMemberResponse::from)
                .toList();
        return ApiResponse.ok(mine);
    }

    @GetMapping("/{id}/members")
    public ApiResponse<List<ChannelMemberResponse>> members(@PathVariable UUID id) {
        List<ChannelMemberResponse> list = channelService.listMembers(id).stream()
                .map(ChannelMemberResponse::from)
                .toList();
        return ApiResponse.ok(list);
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<ApiResponse<ChannelMemberResponse>> addMember(
            @PathVariable UUID id,
            @Valid @RequestBody AddMemberRequest req,
            HttpServletRequest http) {

        ChannelMember m = channelService.addMember(id, req.userId(), currentUserId(http));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Member added", ChannelMemberResponse.from(m)));
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ApiResponse<Void> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID userId,
            HttpServletRequest http) {

        channelService.removeMember(id, userId, currentUserId(http), currentRole(http));
        return ApiResponse.ok("Member removed", null);
    }

    // ----- JWT helpers -----

    private UUID currentUserId(HttpServletRequest http) {
        return jwtService.getUserId(extractToken(http));
    }

    private Role currentRole(HttpServletRequest http) {
        return Role.valueOf(jwtService.getRole(extractToken(http)));
    }

    private String extractToken(HttpServletRequest http) {
        String header = http.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing token");
        }
        return header.substring(7);
    }
}
