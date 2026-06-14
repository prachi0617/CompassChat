package com.compasschat.notification;

import com.compasschat.common.base.BaseService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationService extends BaseService<Notification, UUID> {

    private final NotificationRepository notifications;

    public NotificationService(NotificationRepository notifications) {
        super(notifications, "Notification");
        this.notifications = notifications;
    }

    public String getStatus() {
        return "Notification Service Active";
    }
}
