package com.teamresourceful.resourcefulbees.platform.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface ItemAction {

    static ItemAction get(String name) {
        return ItemActionHelper.get(name);
    }

    String getId();

    TagKey<Item> getTag();

    default boolean test(ItemStack stack) {
        if (stack.getItem() instanceof ItemExtension extension) {
            return extension.canPerformAction(stack, this);
        }
        return stack.is(getTag());
    }

    default boolean test(ItemStack stack, BlockState state, BlockPos pos, BlockGetter level) {
        return test(stack);
    }

}
