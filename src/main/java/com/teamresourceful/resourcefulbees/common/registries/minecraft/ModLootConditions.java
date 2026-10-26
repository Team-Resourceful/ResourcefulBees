package com.teamresourceful.resourcefulbees.common.registries.minecraft;

import com.mojang.serialization.MapCodec;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.loot.condition.BeeHasNectarCondition;
import com.teamresourceful.resourcefulbees.common.registries.RegistryHelper;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ModLootConditions {
    private ModLootConditions() {
    }

    public static final ResourcefulRegistry<MapCodec<? extends LootItemCondition>> LOOT_CONDITIONS = RegistryHelper.create(BuiltInRegistries.LOOT_CONDITION_TYPE, ModConstants.MOD_ID);

    public static final RegistryEntry<MapCodec<? extends LootItemCondition>> BEE_HAS_NECTAR = LOOT_CONDITIONS.register("bee_has_nectar", () -> BeeHasNectarCondition.CODEC);
}