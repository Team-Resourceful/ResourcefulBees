package com.teamresourceful.resourcefulbees.client.events;

import com.teamresourceful.resourcefulbees.events.base.EventHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public record RegisterItemPropertiesEvent(Registrar registrar) {

    public static final EventHelper<RegisterItemPropertiesEvent> EVENT = new EventHelper<>();

    //todo find out what happened to clamped item property function
    public void register(Item item, Identifier name, ClampedItemPropertyFunction property) {
        registrar.register(item, name, property);
    }

    @FunctionalInterface
    public interface Registrar {
        void register(Item item, Identifier name, ClampedItemPropertyFunction property);
    }
}
