package com.compasschat.user;

import com.compasschat.common.base.ApiResponse;
import com.compasschat.user.dto.UpdateUserRequest;
import com.compasschat.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getUser(@PathVariable UUID id) {
        return ApiResponse.ok(UserResponse.from(userService.findById(id)));
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAllUsers() {
        List<UserResponse> safeCopies = userService.findAll().stream()
                .map(UserResponse::from) // photocopy each card
                .toList();
        return ApiResponse.ok(safeCopies);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateUserRequest req) {
        return ApiResponse.ok("Profile updated",
                UserResponse.from(userService.updateProfile(id, req)));
    }
}
