
package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.core.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class CustomHoneyFluidBlock extends LiquidBlock {

    public final HoneyBottleData honeyData;

    public CustomHoneyFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties, HoneyBottleData honeyData) {
        super(supplier, properties);
        this.honeyData = honeyData;
    }

    public int getHoneyColor() {
        return honeyData.getHoneyColorInt();
    }

    public static int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int tintIndex) {
        CustomHoneyFluidBlock honeycombBlock = ((CustomHoneyFluidBlock) state.getBlock());
        return honeycombBlock.honeyData.isRainbow() ? RainbowColor.getRGB() : honeycombBlock.getHoneyColor();
    }

    @Override
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        if (honeyData.isRainbow())
            world.sendBlockUpdated(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }
}
