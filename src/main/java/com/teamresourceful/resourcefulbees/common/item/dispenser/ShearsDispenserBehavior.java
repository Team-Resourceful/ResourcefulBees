package com.teamresourceful.resourcefulbees.common.item.dispenser;

import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ShearsDispenserBehavior extends DefaultDispenseItemBehavior {

    private static DispenseItemBehavior defaultShearsDispenseBehavior;

    public static DispenseItemBehavior getDefaultShearsDispenseBehavior() {
        return defaultShearsDispenseBehavior;
    }

    public static void setDefaultShearsDispenseBehavior(DispenseItemBehavior defaultShearsDispenseBehavior) {
        ShearsDispenserBehavior.defaultShearsDispenseBehavior = defaultShearsDispenseBehavior;
    }

    @NotNull
    @Override
    protected ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
        ServerLevel world = source.getLevel();
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
                    ((BeehiveBlock) blockstate.getBlock()).releaseBeesAndResetHoneyLevel(world, blockstate, blockpos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                }
            }
        } else {
            return getDefaultShearsDispenseBehavior().dispense(source, stack);
        }
        return stack;
    }
}
