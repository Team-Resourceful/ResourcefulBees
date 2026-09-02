package com.teamresourceful.resourcefulbees.datagen.providers.recipes;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.crafting.Ingredient;

public final class RecipeHelper {

    private RecipeHelper() {
    }

    public static ShapedRecipeBuilder boxed(
            ShapedRecipeBuilder builder,
            Ingredient middle,
            Ingredient sides
    ) {
        return builder
                .define('S', sides)
                .define('M', middle)
                .pattern("SSS")
                .pattern("SMS")
                .pattern("SSS");
    }

    public static ShapedRecipeBuilder cornerWithMid(
            ShapedRecipeBuilder builder,
            Ingredient middle,
            Ingredient corners,
            Ingredient sides
    ) {
        return builder
                .define('C', corners)
                .define('S', sides)
                .define('M', middle)
                .pattern("CSC")
                .pattern("SMS")
                .pattern("CSC");
    }
}