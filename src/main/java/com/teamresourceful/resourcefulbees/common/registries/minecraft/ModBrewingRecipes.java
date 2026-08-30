package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.teamresourceful.resourcefulbees.common.brewing.PotionIngredientBrewingRecipe;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

public final class ModBrewingRecipes {

    private static final TagKey<Item> HONEY_BOTTLES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "honey_bottles"));

    private ModBrewingRecipes() throws UtilityClassException {
        throw new UtilityClassException();
    }

    @SubscribeEvent
    public static void register(RegisterBrewingRecipesEvent event) {
        var builder = event.getBuilder();
        builder.addRecipe(new PotionIngredientBrewingRecipe(Potions.AWKWARD, HONEY_BOTTLES, ModPotions.CALMING_POTION.holder()));
        builder.addMix(ModPotions.CALMING_POTION.holder(), Items.REDSTONE, ModPotions.LONG_CALMING_POTION.holder());
    }
}