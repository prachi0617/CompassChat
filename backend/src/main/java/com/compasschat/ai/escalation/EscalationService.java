package com.compasschat.ai.escalation;

import com.compasschat.ai.dto.EscalateResponse;
import com.compasschat.common.enums.Role;
import com.compasschat.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EscalationService {

    private final EscalationLogRepository logs;
    private final UserRepository users;

    public EscalationService(EscalationLogRepository logs, UserRepository users) {
        this.logs = logs;
        this.users = users;
    }

    @Transactional
    public EscalateResponse escalate(UUID userId, String contextMessage) {
        logs.save(new EscalationLog(userId, contextMessage));

        return users.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN || u.getRole() == Role.MODERATOR)
                .findFirst()
                .map(u -> new EscalateResponse(u.getId(), u.getUsername(), "Admin Team"))
                .orElse(new EscalateResponse(null, "Admin Team", "Admin Team"));
    }
}
