package com.teamresourceful.resourcefulbees.platform.common.events.lifecycle;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;

public record LoadingCompletedEvent() {

    private static final LoadingCompletedEvent INSTANCE = new LoadingCompletedEvent();

    public static final EventHelper<LoadingCompletedEvent> EVENT = new EventHelper<>();

    public static void fire() {
        EVENT.fire(INSTANCE);
    }
}
