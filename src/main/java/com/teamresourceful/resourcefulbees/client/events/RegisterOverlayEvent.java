package com.teamresourceful.resourcefulbees.client.events;

import com.teamresourceful.resourcefulbees.client.rendering.OverlayRenderer;
import com.teamresourceful.resourcefulbees.events.base.EventHelper;

import java.util.function.BiConsumer;

public record RegisterOverlayEvent(BiConsumer<String, OverlayRenderer> registry) {

    public static final EventHelper<RegisterOverlayEvent> EVENT = new EventHelper<>();

    public void register(String name, OverlayRenderer renderer) {
        registry.accept(name, renderer);
    }
}
