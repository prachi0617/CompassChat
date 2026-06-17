package com.compasschat.websocket;

import com.compasschat.common.enums.PresenceStatus;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


@Component
public class PresenceEventListener {

    private final PresenceService presenceService;

    public PresenceEventListener(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    @EventListener
    public void onConnected(SessionConnectedEvent event) {
        StompPrincipal p = principal(event.getUser());
        if (p == null) return;
        presenceService.setStatus(p.userId(), PresenceStatus.ONLINE);
    }

    @EventListener
    public void onDisconnected(SessionDisconnectEvent event) {
        StompPrincipal p = principal(event.getUser());
        if (p == null) return;
        presenceService.setStatus(p.userId(), PresenceStatus.OFFLINE);
    }

    private StompPrincipal principal(java.security.Principal raw) {
        if (raw instanceof UsernamePasswordAuthenticationToken auth
                && auth.getPrincipal() instanceof StompPrincipal p) {
            return p;
        }
        return null;
    }
}
