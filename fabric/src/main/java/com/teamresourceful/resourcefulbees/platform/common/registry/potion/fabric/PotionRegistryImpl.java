package com.teamresourceful.resourcefulbees.platform.common.registry.potion.fabric;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class PotionRegistryImpl {

    public static void registerItemRecipe(@NotNull Item from, @NotNull Ingredient ingredient, @NotNull Item to) {
        if (!(from instanceof PotionItem fromPotion)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey(from));
        } else if (!(to instanceof PotionItem toPotion)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey(to));
        } else {
            FabricBrewingRecipeRegistry.registerItemRecipe(fromPotion, ingredient, toPotion);
        }
    }

    public static void registerPotionRecipe(@NotNull Potion from, @NotNull Ingredient ingredient, @NotNull Potion to) {
        FabricBrewingRecipeRegistry.registerPotionRecipe(from, ingredient, to);
    }
}
