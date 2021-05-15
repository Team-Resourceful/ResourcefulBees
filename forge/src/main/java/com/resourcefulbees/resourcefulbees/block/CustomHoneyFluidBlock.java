
package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Supplier;

public class CustomHoneyFluidBlock extends LiquidBlock {

    public final HoneyBottleData honeyData;

    public CustomHoneyFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties, HoneyBottleData honeyData) {
        super(supplier, properties);
        this.honeyData = honeyData;
    }

    public int getHoneyColor() {
        return honeyData.getColorData().getColor().getValue();
    }

    public static int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int tintIndex) {
        CustomHoneyFluidBlock honeycombBlock = ((CustomHoneyFluidBlock) state.getBlock());
        return honeycombBlock.getHoneyColor();
    }

    @Override
    public void animateTick(@NotNull BlockState stateIn, @NotNull Level world, @NotNull BlockPos pos, @NotNull Random rand) {
        if (honeyData.getColorData().isRainbow())
            world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }
}
