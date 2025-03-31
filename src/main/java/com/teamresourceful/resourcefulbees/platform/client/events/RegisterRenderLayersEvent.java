package com.teamresourceful.resourcefulbees.platform.client.events;

import com.teamresourceful.resourcefulbees.platform.common.events.base.EventHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.function.BiConsumer;
import java.util.stream.Stream;

public record RegisterRenderLayersEvent(BiConsumer<Block, RenderType> blockRegistry, BiConsumer<Fluid, RenderType> fluidRegistry) {

    public static final EventHelper<RegisterRenderLayersEvent> EVENT = new EventHelper<>();

    public void registerBlock(Block block, RenderType renderType) {
        blockRegistry.accept(block, renderType);
    }

    public void registerBlocks(Stream<Block> blocks, RenderType renderType) {
        blocks.forEach(block -> registerBlock(block, renderType));
    }

    public void registerFluid(Fluid fluid, RenderType renderType) {
        fluidRegistry.accept(fluid, renderType);
    }

    public void registerFluids(Stream<Fluid> fluids, RenderType renderType) {
        fluids.forEach(fluid -> registerFluid(fluid, renderType));
    }
}
