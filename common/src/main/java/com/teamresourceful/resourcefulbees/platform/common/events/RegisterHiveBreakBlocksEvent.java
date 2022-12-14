package com.teamresourceful.resourcefulbees.platform.common.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public record RegisterHiveBreakBlocksEvent(BiConsumer<Supplier<? extends Block>, Supplier<? extends Block>> registrar) {

    public static final EventHelper<RegisterHiveBreakBlocksEvent> EVENT = new EventHelper<>();

    public void register(Supplier<? extends Block> input, Supplier<? extends Block> output) {
        registrar.accept(input, output);
    }
}
