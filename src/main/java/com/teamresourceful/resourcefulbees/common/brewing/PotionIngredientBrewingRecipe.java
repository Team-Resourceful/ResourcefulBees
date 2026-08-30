package com.teamresourceful.resourcefulbees.common.brewing;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public record PotionIngredientBrewingRecipe(
        Holder<Potion> inputPotion,
        TagKey<Item> brewingIngredient,
        Holder<Potion> outputPotion
) implements IBrewingRecipe {

    @Override
    public boolean isInput(@NotNull ItemStack input) {
        return input.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                .potion()
                .filter(inputPotion::equals)
                .isPresent();
    }

    @Override
    public boolean isIngredient(@NonNull ItemStack ingredient) {
        return ingredient.is(brewingIngredient);
    }

    @Override
    public @NonNull ItemStack getOutput(@NonNull ItemStack input, @NonNull ItemStack ingredient) {
        if (!isInput(input) || !isIngredient(ingredient)) {
            return ItemStack.EMPTY;
        }
        return PotionContents.createItemStack(input.getItem(), outputPotion);
    }
}