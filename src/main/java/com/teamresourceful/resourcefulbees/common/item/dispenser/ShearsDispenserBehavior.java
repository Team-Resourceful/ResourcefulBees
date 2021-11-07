package com.teamresourceful.resourcefulbees.common.item.dispenser;

import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
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

    private static IDispenseItemBehavior defaultShearsDispenseBehavior;

    public static IDispenseItemBehavior getDefaultShearsDispenseBehavior() {
        return defaultShearsDispenseBehavior;
    }

    public static void setDefaultShearsDispenseBehavior(IDispenseItemBehavior defaultShearsDispenseBehavior) {
        ShearsDispenserBehavior.defaultShearsDispenseBehavior = defaultShearsDispenseBehavior;
    }

    @NotNull
    @Override
    protected ItemStack execute(@NotNull IBlockSource source, @NotNull ItemStack stack) {
        ServerWorld world = source.getLevel();
        BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof TieredBeehiveBlock) {
            if (Boolean.TRUE.equals(CommonConfig.ALLOW_SHEARS.get())) {
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
