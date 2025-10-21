package com.teamresourceful.resourcefulbees.platform.common.events.registry;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;

public record RegisterRepositorySourceEvent(PackType type, Consumer<RepositorySource> registry) {

    public static final EventHelper<RegisterRepositorySourceEvent> EVENT = new EventHelper<>();

    public void register(RepositorySource source) {
        registry.accept(source);
    }
}
