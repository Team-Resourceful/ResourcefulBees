package com.teamresourceful.resourcefulbees.platform.common.events.lifecycle;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.server.MinecraftServer;

public record GameServerStartedEvent(MinecraftServer server) {

    public static final EventHelper<GameServerStartedEvent> EVENT = new EventHelper<>();
}
