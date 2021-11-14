package com.resourcefulbees.resourcefulbees.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.brewing.BrewingRecipe;
import org.jetbrains.annotations.NotNull;

public class TagPotionRecipe extends BrewingRecipe {
    @NotNull
    private final ITag<Item> tag;

    public TagPotionRecipe(@NotNull Ingredient input, @NotNull ITag<Item> tag, @NotNull ItemStack output) {
        super(input, Ingredient.EMPTY, output);
        this.tag = tag;
    }

    @Override
    public boolean isInput(@NotNull ItemStack stack) {
        return getInput().test(stack);
    }

    @Override
    public @NotNull ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
        return isInput(input) && isIngredient(ingredient) ? getOutput().copy() : ItemStack.EMPTY;
    }

    @Override
    public @NotNull Ingredient getIngredient() {
        return Ingredient.of(tag);
    }

    @Override
    public boolean isIngredient(@NotNull ItemStack stack) {
        return stack.getItem().is(tag);
    }
}