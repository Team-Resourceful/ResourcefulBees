package com.teamresourceful.resourcefulbees.platform.common.registry.potion.fabric;

import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

//TODO replace with this when this gets merged https://github.com/FabricMC/fabric/pull/2670
public class PotionRegistryImpl {

    public static void registerItemRecipe(@NotNull Item from, @NotNull Ingredient ingredient, @NotNull Item to) {
        if (!(from instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getKey(from));
        } else if (!(to instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getKey(to));
        } else {
            PotionBrewing.CONTAINER_MIXES.add(new PotionBrewing.Mix<>(from, ingredient, to));
        }
    }

    public static void registerPotionRecipe(@NotNull Potion from, @NotNull Ingredient ingredient, @NotNull Potion to) {
        PotionBrewing.POTION_MIXES.add(new PotionBrewing.Mix<>(from, ingredient, to));
    }
}
