package com.compasschat.auth;

import com.compasschat.auth.dto.AuthResponse;
import com.compasschat.auth.dto.LoginRequest;
import com.compasschat.auth.dto.RegisterRequest;
import com.compasschat.common.base.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse result = authService.register(req);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Welcome to CompassChat!", result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse result = authService.login(req);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", result));
    }
}