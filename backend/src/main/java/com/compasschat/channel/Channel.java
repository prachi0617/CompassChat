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
    public ChannelType getType() { return type; }
    public boolean isArchived() { return archived; }
}
