package com.compasschat.volunteer;

import com.compasschat.common.base.AuditableEntity;
import jakarta.persistence.Entity;

@Entity
public class VolunteerSupportRequest extends AuditableEntity {

    private String requestType;
    private String description;
    private String status;

    public VolunteerSupportRequest() {}

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
