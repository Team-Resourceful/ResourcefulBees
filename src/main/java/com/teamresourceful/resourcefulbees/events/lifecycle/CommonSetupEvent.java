package com.teamresourceful.resourcefulbees.events.lifecycle;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;

public record CommonSetupEvent() {

    private static final CommonSetupEvent INSTANCE = new CommonSetupEvent();

    public static final EventHelper<CommonSetupEvent> EVENT = new EventHelper<>();

    public static void fire() {
        EVENT.fire(INSTANCE);
    }
}
