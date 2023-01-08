package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.forge;

import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredient;
import net.minecraft.world.item.crafting.Ingredient;

public class IngredientHelperImpl {

    public static <T extends CodecIngredient<T>> Ingredient getIngredient(T ingredient) {
        return new ForgeIngredient<>(ingredient);
    }
}
