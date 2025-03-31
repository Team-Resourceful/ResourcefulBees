package com.teamresourceful.resourcefulbees.platform.common.events.lifecycle;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

public record ServerGoingToStartEvent(MinecraftServer server, RegistryAccess access) {

    public static final EventHelper<ServerGoingToStartEvent> EVENT = new EventHelper<>();
}
