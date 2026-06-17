package com.compasschat.notification;

import com.compasschat.common.base.AuditableEntity;
import jakarta.persistence.Entity;

@Entity
public class Notification extends AuditableEntity {

    private String title;
    private String message;
    private boolean read;

    public Notification() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
