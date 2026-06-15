package com.compasschat.common.base;

import java.util.UUID;

public abstract class SubProjectClient {

    protected final String projectName;

    protected SubProjectClient(String projectName) {
        this.projectName = projectName;
    }

    public abstract Object fetchContext(UUID userId);

    public String getProjectName() {
        return projectName;
    }
}
