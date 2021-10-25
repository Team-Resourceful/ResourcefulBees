
package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyFluidData;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Supplier;

public class CustomHoneyFluidBlock extends FlowingFluidBlock {

    public final HoneyFluidData data;

    public CustomHoneyFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties, HoneyFluidData data) {
        super(supplier, properties);
        this.data = data;
    }

    public int getHoneyColor() {
        return data.getColor().getValue();
    }

    public static int getBlockColor(BlockState state, @Nullable IWorldReader world, @Nullable BlockPos pos, int tintIndex) {
        return ((CustomHoneyFluidBlock) state.getBlock()).getHoneyColor();
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull World world, @NotNull BlockPos pos, @NotNull Random rand) {
        if (data.getColor().isRainbow()) world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }
}
