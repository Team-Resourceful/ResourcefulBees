package com.teamresourceful.resourcefulbees.events.lifecycle;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;
import net.minecraft.server.MinecraftServer;

public record GameServerStartedEvent(MinecraftServer server) {

    public static final EventHelper<GameServerStartedEvent> EVENT = new EventHelper<>();
}
