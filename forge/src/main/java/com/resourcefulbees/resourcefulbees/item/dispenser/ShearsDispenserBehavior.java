package com.resourcefulbees.resourcefulbees.item.dispenser;

import com.resourcefulbees.resourcefulbees.block.TieredBeehiveBlock;
import com.resourcefulbees.resourcefulbees.config.Config;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;

public class ShearsDispenserBehavior extends DefaultDispenseItemBehavior {

    private static IDispenseItemBehavior DEFAULT_SHEARS_DISPENSE_BEHAVIOR;

    public static IDispenseItemBehavior getDefaultShearsDispenseBehavior() {
        return DEFAULT_SHEARS_DISPENSE_BEHAVIOR;
    }

    public static void setDefaultShearsDispenseBehavior(IDispenseItemBehavior defaultShearsDispenseBehavior) {
        DEFAULT_SHEARS_DISPENSE_BEHAVIOR = defaultShearsDispenseBehavior;
    }

    @Nonnull
    @Override
    protected ItemStack execute(@Nonnull IBlockSource source, @Nonnull  ItemStack stack) {
        ServerWorld world = source.getLevel();
        BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof TieredBeehiveBlock) {
            if (Config.ALLOW_SHEARS.get()) {
                int i = blockstate.getValue(BeehiveBlock.HONEY_LEVEL);
                if (i >= 5) {
                    if (stack.hurt(1, world.random, null)) {
                        stack.setCount(0);
                    }

                    TieredBeehiveBlock.dropResourceHoneycomb((TieredBeehiveBlock) blockstate.getBlock(), world, blockpos, false);
                    ((BeehiveBlock) blockstate.getBlock()).releaseBeesAndResetHoneyLevel(world, blockstate, blockpos, null, BeehiveTileEntity.State.BEE_RELEASED);
                }
            }
        } else {
            return getDefaultShearsDispenseBehavior().dispense(source, stack);
        }
        return stack;
    }
}
