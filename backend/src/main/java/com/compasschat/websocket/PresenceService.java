package com.compasschat.websocket;

import com.compasschat.common.enums.PresenceStatus;
import com.compasschat.user.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    private final ConcurrentHashMap<UUID, PresenceStatus> statusMap = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate broker;
    private final UserRepository users;

    public PresenceService(SimpMessagingTemplate broker, UserRepository users) {
        this.broker = broker;
        this.users = users;
    }

    public void setStatus(UUID userId, PresenceStatus status) {
        statusMap.put(userId, status);
        String username = users.findById(userId)
                .map(u -> u.getUsername())
                .orElse(userId.toString());
        broker.convertAndSend("/topic/presence",
                new PresenceUpdate(userId, username, status, LocalDateTime.now()));
    }

    public PresenceStatus getStatus(UUID userId) {
        return statusMap.getOrDefault(userId, PresenceStatus.OFFLINE);
    }
}
