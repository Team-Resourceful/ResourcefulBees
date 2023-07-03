package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public record RegisterItemPropertiesEvent(Registrar registrar) {

    public static final EventHelper<RegisterItemPropertiesEvent> EVENT = new EventHelper<>();

    public void register(Item item, ResourceLocation name, ClampedItemPropertyFunction property) {
        registrar.register(item, name, property);
    }

    @FunctionalInterface
    public interface Registrar {
        void register(Item item, ResourceLocation name, ClampedItemPropertyFunction property);
    }
}
