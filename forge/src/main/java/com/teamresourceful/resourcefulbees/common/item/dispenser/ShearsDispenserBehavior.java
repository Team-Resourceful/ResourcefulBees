package com.teamresourceful.resourcefulbees.common.item.dispenser;

import com.teamresourceful.resourcefulbees.common.blocks.TieredBeehiveBlock;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
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

    private final DispenseItemBehavior defaultBehavior;

    public ShearsDispenserBehavior(DispenseItemBehavior defaultBehavior) {
        this.defaultBehavior = defaultBehavior;
    }

    @NotNull
    @Override
    protected ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
        ServerLevel world = source.getLevel();
        BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof TieredBeehiveBlock tieredBeehiveBlock) {
            if (GeneralConfig.allowShears) {
                int i = blockstate.getValue(BeehiveBlock.HONEY_LEVEL);
                if (i >= 5) {
                    if (stack.hurt(1, world.random, null)) {
                        stack.setCount(0);
                    }

                    TieredBeehiveBlock.dropResourceHoneycomb(tieredBeehiveBlock, world, blockpos, false);
                    tieredBeehiveBlock.releaseBeesAndResetHoneyLevel(world, blockstate, blockpos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                }
            }
        } else if (defaultBehavior != DispenseItemBehavior.NOOP) {
            return defaultBehavior.dispense(source, stack);
        }
        return stack;
    }
}
