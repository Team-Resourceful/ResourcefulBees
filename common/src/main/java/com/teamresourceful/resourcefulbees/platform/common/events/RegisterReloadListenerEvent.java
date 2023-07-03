package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public record RegisterReloadListenerEvent(Consumer<PreparableReloadListener> registry, ReloadableServerResources resources) {

    public static final EventHelper<RegisterReloadListenerEvent> EVENT = new EventHelper<>();

    public void register(PreparableReloadListener listener) {
        registry.accept(listener);
    }
}
