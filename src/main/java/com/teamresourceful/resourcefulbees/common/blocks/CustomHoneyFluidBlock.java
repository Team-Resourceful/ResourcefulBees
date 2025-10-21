package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import earth.terrarium.botarium.common.registry.fluid.BotariumLiquidBlock;
import earth.terrarium.botarium.common.registry.fluid.FluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CustomHoneyFluidBlock extends BotariumLiquidBlock {

    public final HoneyFluidData data;

    public CustomHoneyFluidBlock(FluidData data, Properties properties, HoneyFluidData honeyData) {
        super(data, properties);
        this.data = honeyData;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (data.renderData().color().isRainbow()) world.sendBlockUpdated(pos, stateIn, stateIn, Block.UPDATE_CLIENTS);
        super.animateTick(stateIn, world, pos, rand);
    }
}