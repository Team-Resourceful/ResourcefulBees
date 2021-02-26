
package com.resourcefulbees.resourcefulbees.block;

import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.function.Supplier;

public class CustomHoneyFluidBlock extends FlowingFluidBlock {

    public final HoneyBottleData honeyData;

    public CustomHoneyFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties, HoneyBottleData honeyData) {
        super(supplier, properties);
        this.honeyData = honeyData;
    }

    public int getHoneyColor() {
        return honeyData.getHoneyColorInt();
    }

    public static int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int tintIndex) {
        CustomHoneyFluidBlock honeycombBlock = ((CustomHoneyFluidBlock) state.getBlock());
        return honeycombBlock.honeyData.isRainbow() ? RainbowColor.getRGB() : honeycombBlock.getHoneyColor();
    }

    @Override
    public void animateTick(@Nonnull BlockState stateIn, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
        if (honeyData.isRainbow())
            world.notifyBlockUpdate(pos, stateIn, stateIn, 2);
        super.animateTick(stateIn, world, pos, rand);
    }
}
