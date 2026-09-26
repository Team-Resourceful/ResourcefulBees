package com.teamresourceful.resourcefulbees.datagen.providers.loottables;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class RBeesLootTableProvider extends LootTableProvider {

    public RBeesLootTableProvider() {
        super(Set.of(), List.of(new SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK)));
    }
}