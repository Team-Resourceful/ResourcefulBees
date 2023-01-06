package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient;

import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.function.Predicate;

public interface CodecIngredient<T extends CodecIngredient<T>> extends Predicate<ItemStack> {

    @Override
    boolean test(ItemStack stack);

    Collection<ItemStack> getStacks();

    /**
     * Determines if this ingredient is constant.
     * i.e. it will always return the same stacks such that does not depend on nbt or other data.
     * @return true if this ingredient is constant.
     */
    default boolean isConstant() {
        return true;
    }

    CodecIngredientSerializer<T> serializer();
}
