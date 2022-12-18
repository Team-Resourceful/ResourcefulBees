package com.teamresourceful.resourcefulbees.platform.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface ItemExtension {

    default boolean canPerformAction(ItemStack stack, ItemAction action) {
        return action.test(stack);
    }

    default boolean canPerformAction(ItemStack stack, BlockState state, BlockPos pos, BlockGetter level, ItemAction action) {
        return action.test(stack, state, pos, level);
    }
}
