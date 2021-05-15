package com.teamresourceful.resourcefulbees.item.dispenser;

import com.teamresourceful.resourcefulbees.block.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.config.Config;
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

    private static DispenseItemBehavior DEFAULT_SHEARS_DISPENSE_BEHAVIOR;

    public static DispenseItemBehavior getDefaultShearsDispenseBehavior() {
        return DEFAULT_SHEARS_DISPENSE_BEHAVIOR;
    }

    public static void setDefaultShearsDispenseBehavior(DispenseItemBehavior defaultShearsDispenseBehavior) {
        DEFAULT_SHEARS_DISPENSE_BEHAVIOR = defaultShearsDispenseBehavior;
    }

    @NotNull
    @Override
    protected ItemStack execute(@NotNull BlockSource source, @NotNull  ItemStack stack) {
        ServerLevel world = source.getLevel();
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
                    ((BeehiveBlock) blockstate.getBlock()).releaseBeesAndResetHoneyLevel(world, blockstate, blockpos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                }
            }
        } else {
            return getDefaultShearsDispenseBehavior().dispense(source, stack);
        }
        return stack;
    }
}
