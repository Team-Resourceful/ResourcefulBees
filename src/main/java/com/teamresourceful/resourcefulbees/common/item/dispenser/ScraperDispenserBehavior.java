package com.teamresourceful.resourcefulbees.common.item.dispenser;

import com.teamresourceful.resourcefulbees.common.block.TieredBeehiveBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ScraperDispenserBehavior extends DefaultDispenseItemBehavior {

    @NotNull
    @Override
    protected ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
        ServerLevel world = source.getLevel();
        BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
        BlockState blockstate = world.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof TieredBeehiveBlock) {
            int i = blockstate.getValue(BeehiveBlock.HONEY_LEVEL);
            if (i >= 5) {
                if (stack.hurt(1, world.random, null)) {
                    stack.setCount(0);
                }

                if (TieredBeehiveBlock.dropResourceHoneycomb((TieredBeehiveBlock) blockstate.getBlock(), world, blockpos, true)) {
                    ((BeehiveBlock) blockstate.getBlock()).releaseBeesAndResetHoneyLevel(world, blockstate, blockpos, null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
                }
            }
        }
        return stack;
    }
}
