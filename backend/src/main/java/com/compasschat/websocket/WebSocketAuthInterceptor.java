package com.compasschat.websocket;

import com.compasschat.auth.JwtService;
import com.compasschat.channel.ChannelService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final ChannelService channelService;

    public WebSocketAuthInterceptor(JwtService jwtService, ChannelService channelService) {
        this.jwtService = jwtService;
        this.channelService = channelService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() == null) return message;

        switch (accessor.getCommand()) {
            case CONNECT   -> handleConnect(accessor);
            case SUBSCRIBE -> handleSubscribe(accessor);
            case SEND      -> handleSend(accessor);
            default        -> { /* HEARTBEAT, DISCONNECT, ACK: nothing */ }
        }
        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String header = accessor.getFirstNativeHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing or malformed Authorization header");
        }
        String token = header.substring(7);
        if (!jwtService.isValid(token)) {
            throw new IllegalArgumentException("Invalid or expired token");
        }

        UUID userId = jwtService.getUserId(token);
        String username = jwtService.getUsername(token);
        String role = jwtService.getRole(token);

        accessor.setUser(new UsernamePasswordAuthenticationToken(
                new StompPrincipal(userId, username),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        UUID channelId = parseChannelTopic(accessor.getDestination());
        if (channelId == null) return; // not a channel topic - let it through

        if (!channelService.canUserAccess(channelId, currentUserId(accessor))) {
            throw new IllegalArgumentException("Not allowed to subscribe to this channel");
        }
    }

    private void handleSend(StompHeaderAccessor accessor) {
        UUID channelId = parseAppChannel(accessor.getDestination());
        if (channelId == null) return;

        if (!channelService.canUserAccess(channelId, currentUserId(accessor))) {
            throw new IllegalArgumentException("Not allowed to send to this channel");
        }
    }

    private UUID currentUserId(StompHeaderAccessor accessor) {
        if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken auth
                && auth.getPrincipal() instanceof StompPrincipal p) {
            return p.userId();
        }
        throw new IllegalArgumentException("Not authenticated");
    }

    /** Matches /topic/channels/{uuid} and /topic/channels/{uuid}/typing */
    private UUID parseChannelTopic(String destination) {
        if (destination == null || !destination.startsWith("/topic/channels/")) return null;
        String tail = destination.substring("/topic/channels/".length()).split("/")[0];
        try { return UUID.fromString(tail); } catch (Exception e) { return null; }
    }

    /** Matches /app/channels/{uuid}/messages and /app/channels/{uuid}/typing */
    private UUID parseAppChannel(String destination) {
        if (destination == null || !destination.startsWith("/app/channels/")) return null;
        String tail = destination.substring("/app/channels/".length()).split("/")[0];
        try { return UUID.fromString(tail); } catch (Exception e) { return null; }
    }
}

