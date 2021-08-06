package com.teamresourceful.resourcefulbees.common.item.dispenser;

import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.config.Config;
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
import org.jetbrains.annotations.NotNull;

public class ShearsDispenserBehavior extends DefaultDispenseItemBehavior {

    private static IDispenseItemBehavior DEFAULT_SHEARS_DISPENSE_BEHAVIOR;

    public static IDispenseItemBehavior getDefaultShearsDispenseBehavior() {
        return DEFAULT_SHEARS_DISPENSE_BEHAVIOR;
    }

    public static void setDefaultShearsDispenseBehavior(IDispenseItemBehavior defaultShearsDispenseBehavior) {
        DEFAULT_SHEARS_DISPENSE_BEHAVIOR = defaultShearsDispenseBehavior;
    }

    @NotNull
    @Override
    protected ItemStack execute(@NotNull IBlockSource source, @NotNull ItemStack stack) {
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
