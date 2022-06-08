
package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyFluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CustomHoneyFluidBlock extends LiquidBlock {

    public final HoneyFluidData data;

    public CustomHoneyFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties, HoneyFluidData data) {
        super(supplier, properties);
        this.data = data;
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        if (data.color().isRainbow()) world.sendBlockUpdated(pos, stateIn, stateIn, Block.UPDATE_CLIENTS);
        super.animateTick(stateIn, world, pos, rand);
    }
}
