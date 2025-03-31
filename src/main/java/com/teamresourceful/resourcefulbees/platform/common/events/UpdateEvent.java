package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;

public record UpdateEvent(UpdateType type) {

    public static final EventHelper<UpdateEvent> EVENT = new EventHelper<>();

    public enum UpdateType {
        RECIPE,
        TAG
    }
}
