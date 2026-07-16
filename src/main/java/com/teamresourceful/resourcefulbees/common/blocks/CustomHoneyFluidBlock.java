package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefulbees.api.data.honey.fluid.HoneyFluidData;
import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CustomHoneyFluidBlock extends LiquidBlock {

    public final HoneyFluidData honeyFluidData;

    public CustomHoneyFluidBlock(FluidData data, Properties properties, HoneyFluidData honeyFluidData) {
        super(data.still().get(), properties);
        data.setBlock(() -> this);
        this.honeyFluidData = honeyFluidData;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (honeyFluidData.renderData().color().isSpecial()) world.sendBlockUpdated(pos, stateIn, stateIn, Block.UPDATE_CLIENTS);
        super.animateTick(stateIn, world, pos, rand);
    }
}