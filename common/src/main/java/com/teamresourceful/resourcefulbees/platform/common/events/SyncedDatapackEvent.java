package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.jetbrains.annotations.Nullable;

/**
 * @param allPlayers
 * @param player can return null when the event is called for all players via /reload
 */
public record SyncedDatapackEvent(PlayerList allPlayers, @Nullable ServerPlayer player) {

    public static final EventHelper<SyncedDatapackEvent> EVENT = new EventHelper<>();
}
