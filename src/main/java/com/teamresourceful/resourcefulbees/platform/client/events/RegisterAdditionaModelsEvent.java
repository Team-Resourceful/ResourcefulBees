package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public record RegisterAdditionaModelsEvent(Consumer<ResourceLocation> registry) {

    public static final EventHelper<RegisterAdditionaModelsEvent> EVENT = new EventHelper<>();

    public void register(ResourceLocation location) {
        registry.accept(location);
    }

}
