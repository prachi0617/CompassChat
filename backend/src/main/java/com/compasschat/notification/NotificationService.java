package com.compasschat.notification;

import com.compasschat.common.service.BaseService;
import com.compasschat.reminder.ReminderDueEvent;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.stereotype.Service;

@Service
public class NotificationService extends BaseService<Notification> {

    public String getStatus() {
        return "Notification Service Active";
    }

@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void onReminderDue(ReminderDueEvent event) {
    notificationService.create(
        event.getUserId(),
        NotificationType.REMINDER,
        "Reminder: " + event.getTitle(),
        "/reminders/" + event.getReminderId()
    );
}
}
