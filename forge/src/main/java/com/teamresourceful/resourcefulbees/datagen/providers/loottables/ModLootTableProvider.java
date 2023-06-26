package com.teamresourceful.resourcefulbees.datagen.providers.loottables;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends LootTableProvider {

    public ModLootTableProvider(DataGenerator generator, CompletableFuture<HolderLookup.Provider> provider) {
        super(generator.getPackOutput(), Set.of(), List.of(
            new SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK)
        ));
    }

    @Override
    protected void validate(@NotNull Map<ResourceLocation, LootTable> map, @NotNull ValidationContext validationtracker) {
        //We dont need validation.
    }
}
