package com.teamresourceful.resourcefulbees.client.events;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

public record RegisterAdditionalModelsEvent(Consumer<Identifier> registry) {

    public static final EventHelper<RegisterAdditionalModelsEvent> EVENT = new EventHelper<>();

    public void register(Identifier location) {
        registry.accept(location);
    }

}
