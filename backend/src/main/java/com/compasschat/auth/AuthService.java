package com.compasschat.auth;

import com.compasschat.auth.dto.AuthResponse;
import com.compasschat.auth.dto.LoginRequest;
import com.compasschat.auth.dto.RegisterRequest;
import com.compasschat.user.User;
import com.compasschat.user.UserRepository;
import com.compasschat.common.enums.Role;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;  // the blender
    private final JwtService jwtService;    // the wristband machine

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwtService) {
        this.users = users;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    /** Rule book for joining the club. */
    public AuthResponse register(RegisterRequest req) {
        if (users.findByUsername(req.username()).isPresent()) {
            throw new IllegalStateException("That username is taken");
        }

        User user = new User(req.username(), encoder.encode(req.password()), Role.MEMBER);
        user = users.save(user); // save returns the card WITH its new UUID filled in

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }

    /** Rule book for getting in the door. */
    public AuthResponse login(LoginRequest req) {
        // Same vague error for "no such user" AND "wrong password",
        // so snoopers can't discover which usernames exist.
        User user = users.findByUsername(req.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!encoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}
