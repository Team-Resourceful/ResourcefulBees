package com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.fabric;

import com.teamresourceful.resourcefulbees.platform.common.recipe.ingredient.CodecIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record FabricIngredient<T extends CodecIngredient<T>>(CodecIngredient<T> ingredient) implements CustomIngredient {

    @Override
    public boolean test(ItemStack stack) {
        return ingredient.test(stack);
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return ingredient.getStacks();
    }

    @Override
    public boolean requiresTesting() {
        return !ingredient.isConstant();
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return null;
    }
}
