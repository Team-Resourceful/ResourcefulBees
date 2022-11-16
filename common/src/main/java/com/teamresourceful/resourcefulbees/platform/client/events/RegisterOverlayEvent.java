package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.client.renderer.overlay.OverlayRenderer;

import java.util.HashMap;
import java.util.Map;

public class RegisterOverlayEvent {

    private final Map<String, OverlayRenderer> renderers = new HashMap<>();

    public void register(String name, OverlayRenderer renderer) {
        renderers.put(name, renderer);
    }

    public Map<String, OverlayRenderer> getRenderers() {
        return renderers;
    }
}
