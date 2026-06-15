package com.compasschat.user;

import com.compasschat.auth.security.JwtService;
import com.compasschat.common.base.ApiResponse;
import com.compasschat.user.dto.PresenceUpdateRequest;
import com.compasschat.user.dto.UpdateUserRequest;
import com.compasschat.user.dto.UserResponse;
import com.compasschat.websocket.PresenceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PresenceService presenceService;

    public UserController(UserService userService, JwtService jwtService, PresenceService presenceService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.presenceService = presenceService;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(HttpServletRequest http) {
        String username = jwtService.getUsername(extractToken(http));
        return ApiResponse.ok(UserResponse.from(userService.findByUsername(username)));
    }

    @PutMapping("/me/presence")
    public ApiResponse<Void> updatePresence(@Valid @RequestBody PresenceUpdateRequest req,
                                            HttpServletRequest http) {
        UUID userId = jwtService.getUserId(extractToken(http));
        presenceService.setStatus(userId, req.status());
        return ApiResponse.ok(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable UUID id) {
        return ApiResponse.ok(UserResponse.from(userService.findById(id)));
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> safeCopies = userService.findAll().stream()
                .map(UserResponse::from)
                .toList();
        return ApiResponse.ok(safeCopies);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateUserRequest req) {
        return ApiResponse.ok("Profile updated",
                UserResponse.from(userService.updateProfile(id, req)));
    }

    private String extractToken(HttpServletRequest http) {
        String header = http.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing Authorization header");
        }
        return header.substring(7);
    }
}
