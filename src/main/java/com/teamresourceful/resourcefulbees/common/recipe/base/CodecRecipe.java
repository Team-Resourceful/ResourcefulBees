package com.teamresourceful.resourcefulbees.common.recipe.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;

public interface CodecRecipe<C extends Container> extends Recipe<C> {

    @NotNull
    ResourceLocation id();

    @Override
    @NotNull
    default ResourceLocation getId() {
        return id();
    }

    @Override
    default boolean isSpecial() {
        return true;
    }

    @Override
    @NotNull
    default ItemStack assemble(@NotNull C pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    @NotNull
    default ItemStack getResultItem() {
        return ItemStack.EMPTY;
    }

    @Override
    default boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }
}
