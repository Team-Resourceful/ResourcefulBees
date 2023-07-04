package com.teamresourceful.resourcefulbees.platform.client.events.registry;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.function.Consumer;

public record RegisterWoodTypeSheetsEvent(Consumer<WoodType> registry) {

    public static final EventHelper<RegisterWoodTypeSheetsEvent> EVENT = new EventHelper<>();

    public void register(WoodType woodType) {
        registry.accept(woodType);
    }

}

