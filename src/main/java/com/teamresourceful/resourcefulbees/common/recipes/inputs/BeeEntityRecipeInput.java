package com.teamresourceful.resourcefulbees.common.recipes.inputs;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jspecify.annotations.NonNull;

public record BeeEntityRecipeInput(Holder.Reference<EntityType<?>> bee) implements RecipeInput {

    @Override
    public @NonNull ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return RecipeInput.super.isEmpty();
    }
}
