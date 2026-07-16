package com.teamresourceful.resourcefulbees.common.blocks;

import com.teamresourceful.resourcefullib.common.fluid.data.FluidData;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class HoneyFluidBlock extends LiquidBlock {

    public HoneyFluidBlock(FluidData data, BlockBehaviour.Properties properties) {
        super(data.still().get(), properties);
        data.setBlock(() -> this);
    }
}
