package com.teamresourceful.resourcefulbees.platform.common.registry.potion.forge;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import org.jetbrains.annotations.NotNull;

public class PotionRegistryImpl {
    public static void registerItemRecipe(@NotNull Item from, @NotNull Ingredient ingredient, @NotNull Item to) {
        BrewingRecipeRegistry.addRecipe(Ingredient.of(from), ingredient, new ItemStack(to));
    }

    public static void registerPotionRecipe(@NotNull Potion from, @NotNull Ingredient ingredient, @NotNull Potion to) {
        ItemStack fromStack = PotionUtils.setPotion(new ItemStack(Items.POTION), from);
        ItemStack toStack = PotionUtils.setPotion(new ItemStack(Items.POTION), to);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(fromStack), ingredient, toStack);
        BrewingRecipeRegistry.addRecipe(Ingredient.of(fromStack), Ingredient.of(Tags.Items.GUNPOWDER), PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), to));
        BrewingRecipeRegistry.addRecipe(Ingredient.of(fromStack), Ingredient.of(Items.DRAGON_BREATH), PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), to));
    }
}
