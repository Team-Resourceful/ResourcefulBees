package com.teamresourceful.resourcefulbees.common.recipe.base;

import net.minecraft.world.item.crafting.RecipeType;

public class CodecRecipeType<T extends CodecRecipe<?>> implements RecipeType<T> {

    private final String id;

    public CodecRecipeType(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[" + id + "]";
    }
}
