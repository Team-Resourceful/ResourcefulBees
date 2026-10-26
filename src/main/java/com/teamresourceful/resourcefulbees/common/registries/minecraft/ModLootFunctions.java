package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.loot.function.RandomPollenRecipeFunction;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

public class ModLootFunctions {
    private ModLootFunctions() {
    }

    public static final ResourcefulRegistry<MapCodec<? extends LootItemFunction>> LOOT_FUNCTIONS = RegistryHelper.create(BuiltInRegistries.LOOT_FUNCTION_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<MapCodec<? extends LootItemFunction>> RANDOM_POLLEN_RECIPE = LOOT_FUNCTIONS.register("random_pollen_recipe", () -> RandomPollenRecipeFunction.CODEC);
}