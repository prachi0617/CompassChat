package com.compasschat.notification;

import com.compasschat.common.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class NotificationService extends BaseService<Notification> {

    public String getStatus() {
        return "Notification Service Active";
    }
}
