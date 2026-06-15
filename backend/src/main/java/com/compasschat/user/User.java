package com.compasschat.user;

import com.compasschat.common.base.AuditableEntity;
import com.compasschat.common.enums.PresenceStatus;
import com.compasschat.common.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users") // "user" is a reserved word in some databases
public class User extends AuditableEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    /** The blended smoothie - never the real password. */
    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PresenceStatus presence = PresenceStatus.OFFLINE;

    @Column
    private String assignedSubProject;

    protected User() {} // JPA's required empty constructor

    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public PresenceStatus getPresence() { return presence; }
    public void setPresence(PresenceStatus presence) { this.presence = presence; }
    public String getAssignedSubProject() { return assignedSubProject; }
    public void setAssignedSubProject(String assignedSubProject) { this.assignedSubProject = assignedSubProject; }
}
