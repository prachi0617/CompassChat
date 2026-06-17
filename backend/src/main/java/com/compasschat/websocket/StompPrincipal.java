package com.compasschat.websocket;

import java.security.Principal;
import java.util.UUID;

public record StompPrincipal(UUID userId, String username) implements Principal {

    @Override
    public String getName() {
        
        return userId.toString();
    }
}

