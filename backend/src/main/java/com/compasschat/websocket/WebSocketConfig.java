package com.compasschat.websocket;

import com.compasschat.security.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtService jwtService;

    public WebSocketConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // The wall socket where the frontend plugs in its cup:
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // demo only! lock this down in production
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");          // the megaphone lives here
        registry.setApplicationDestinationPrefixes("/app"); // user->server mail goes here
    }

    /**
     * Wristband check at the moment someone picks up the cup-phone.
     * The frontend sends the JWT in the CONNECT frame's headers.
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String header = accessor.getFirstNativeHeader("Authorization");

                    if (header == null || !header.startsWith("Bearer ")
                            || !jwtService.isValid(header.substring(7))) {
                        // No valid wristband? You don't get a phone.
                        throw new IllegalArgumentException("Missing or invalid token");
                    }

                    String token = header.substring(7);
                    String username = jwtService.getUsername(token);
                    String role = jwtService.getRole(token);

                    accessor.setUser(new UsernamePasswordAuthenticationToken(
                            username, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))));
                }
                return message;
            }
        });
    }
}