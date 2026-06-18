package com.teamresourceful.resourcefulbees.common.registries;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import org.jetbrains.annotations.NotNull;

public class PotionRegistry {
    //todo switch to RegisterBrewingRecipesEvent
    public static void registerItemRecipe(@NotNull Item from, @NotNull Ingredient ingredient, @NotNull Item to) {
        BrewingRecipeRegistry.addRecipe(Ingredient.of(from), ingredient, new ItemStack(to));
    }
    //todo switch to RegisterBrewingRecipesEvent
    public static void registerPotionRecipe(@NotNull Potion from, @NotNull Ingredient ingredient, @NotNull Potion to) {
        ItemStack fromStack = PotionContents.createItemStack(Items.POTION, Holder.direct(from));
        ItemStack toStack = PotionContents.createItemStack(Items.POTION, Holder.direct(to));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(fromStack), ingredient, toStack);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(fromStack), Ingredient.of(Items.GUNPOWDER), PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), to));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(fromStack), Ingredient.of(Items.DRAGON_BREATH), PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), to));

    }
}
