package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.client.renderer.overlay.OverlayRenderer;
import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;

import java.util.function.BiConsumer;

public record RegisterOverlayEvent(BiConsumer<String, OverlayRenderer> registry) {

    public static final EventHelper<RegisterOverlayEvent> EVENT = new EventHelper<>();

    public void register(String name, OverlayRenderer renderer) {
        registry.accept(name, renderer);
    }
}
