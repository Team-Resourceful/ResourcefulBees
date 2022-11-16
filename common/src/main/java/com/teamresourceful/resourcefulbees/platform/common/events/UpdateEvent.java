package com.teamresourceful.resourcefulbees.platform.common.events;

public class UpdateEvent {

    private final UpdateType type;

    public UpdateEvent(UpdateType type) {
        this.type = type;
    }

    public UpdateType getType() {
        return type;
    }

    public enum UpdateType {
        RECIPE,
        TAG
    }
}
