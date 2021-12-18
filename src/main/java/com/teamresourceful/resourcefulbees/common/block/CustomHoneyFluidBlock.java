
package com.teamresourceful.resourcefulbees.common.block;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyFluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Supplier;

public class CustomHoneyFluidBlock extends LiquidBlock {

    public final HoneyFluidData data;

    public CustomHoneyFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties, HoneyFluidData data) {
        super(supplier, properties);
        this.data = data;
    }

    public int getHoneyColor() {
        return data.color().getValue();
    }

    public static int getBlockColor(BlockState state, @Nullable LevelReader world, @Nullable BlockPos pos, int tintIndex) {
        return ((CustomHoneyFluidBlock) state.getBlock()).getHoneyColor();
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull Random rand) {
        if (data.color().isRainbow()) world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }
}
