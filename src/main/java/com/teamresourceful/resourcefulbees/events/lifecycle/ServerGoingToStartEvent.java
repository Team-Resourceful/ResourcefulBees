package com.teamresourceful.resourcefulbees.events.lifecycle;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;

public record ServerGoingToStartEvent(MinecraftServer server, RegistryAccess access) {

    public static final EventHelper<ServerGoingToStartEvent> EVENT = new EventHelper<>();
}
