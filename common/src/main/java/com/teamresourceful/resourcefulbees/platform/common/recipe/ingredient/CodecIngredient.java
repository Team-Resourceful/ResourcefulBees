package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public interface CodecIngredient<T extends CodecIngredient<T>> extends Predicate<@Nullable ItemStack> {

    @Override
    boolean test(@Nullable ItemStack stack);

    List<ItemStack> getStacks();

    default ItemStack[] getStacksAsArray() {
        return getStacks().toArray(new ItemStack[0]);
    }

    default boolean isEmpty() {
        return getStacks().isEmpty();
    }

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
