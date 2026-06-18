package com.teamresourceful.resourcefulbees.events;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;
import net.minecraft.server.level.ServerPlayer;

/**
 * @param player can return null when the event is called for all players via /reload
 */
public record SyncedDatapackEvent(ServerPlayer player) {

    public static final EventHelper<SyncedDatapackEvent> EVENT = new EventHelper<>();
}
