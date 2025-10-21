package com.teamresourceful.resourcefulbees.platform.client.events.lifecycle;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import org.jetbrains.annotations.ApiStatus;

public record ClientLoadingCompletedEvent() {

    private static final ClientLoadingCompletedEvent INSTANCE = new ClientLoadingCompletedEvent();

    public static final EventHelper<ClientLoadingCompletedEvent> EVENT = new EventHelper<>();

    @ApiStatus.Internal
    public static void fire() {
        EVENT.fire(INSTANCE);
    }
}
