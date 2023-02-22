package com.teamresourceful.resourcefulbees.common.util;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public final class ContainerUtils {

    private ContainerUtils() {
        throw new UtilityClassError();
    }

    public static ItemStack insertItem(Container container, int slot, ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack existing = container.getItem(slot);
        int limit = Math.min(64, stack.getMaxStackSize());

        if (!existing.isEmpty()) {
            if (!canStacksMerge(stack, existing)) {
                return stack;
            }
            limit -= existing.getCount();
        }

        if (limit <= 0) return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (existing.isEmpty()) container.setItem(slot, reachedLimit ? copyWithCount(stack, limit) : stack);
        else existing.grow(reachedLimit ? limit : stack.getCount());

        return reachedLimit ? copyWithCount(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    public static boolean canStacksMerge(ItemStack stack, ItemStack other) {
        if (stack.isEmpty() || other.isEmpty()) return false;
        return ItemStack.isSameItemSameTags(stack, other);
    }

    //TODO Change to copyWithCount in 1.19.3
    public static ItemStack copyWithCount(ItemStack stack, int size) {
        ItemStack copy = stack.copy();
        copy.setCount(size);
        return copy;
    }
}
