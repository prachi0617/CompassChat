package com.compasschat.channel;

import com.compasschat.common.base.AuditableEntity;
import com.compasschat.common.enums.ChannelType;
import jakarta.persistence.*;

@Entity
@Table(name = "channels")
public class Channel extends AuditableEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type = ChannelType.PUBLIC;

    @Column(length = 500)
    private String purpose;

    @Column
    private String linkedSubProject;

    @Column(nullable = false)
    private boolean archived = false;

    protected Channel() {}

    public Channel(String name, String description, ChannelType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public void rename(String name) { this.name = name; }
    public void updateDescription(String description) { this.description = description; }
    public void archive() { this.archived = true; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getLinkedSubProject() { return linkedSubProject; }
    public void setLinkedSubProject(String linkedSubProject) { this.linkedSubProject = linkedSubProject; }
    public ChannelType getType() { return type; }
    public boolean isArchived() { return archived; }
}
