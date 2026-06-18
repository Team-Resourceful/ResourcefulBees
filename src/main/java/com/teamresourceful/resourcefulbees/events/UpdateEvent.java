package com.teamresourceful.resourcefulbees.events;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;

public record UpdateEvent(UpdateType type) {

    public static final EventHelper<UpdateEvent> EVENT = new EventHelper<>();

    public enum UpdateType {
        RECIPE,
        TAG
    }
}
