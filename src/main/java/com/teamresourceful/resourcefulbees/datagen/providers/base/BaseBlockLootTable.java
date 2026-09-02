package com.teamresourceful.resourcefulbees.datagen.providers.base;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public abstract class BaseBlockLootTable extends BlockLootSubProvider {

    private final Set<Block> knownBlocks = new HashSet<>();

    protected BaseBlockLootTable(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    protected void add(@NonNull Block block, LootTable.@NonNull Builder builder) {
        super.add(block, builder);
        knownBlocks.add(block);
    }

    protected void add(RegistryEntry<? extends Block> entry, LootTable.Builder builder) {
        add(entry.get(), builder);
    }

    protected void add(RegistryEntry<? extends Block> entry, Function<Block, LootTable.Builder> factory) {
        add(entry.get(), factory);
    }

    protected void dropSelf(RegistryEntry<? extends Block> entry) {
        dropSelf(entry.get());
    }

    @Override
    protected final @NonNull Iterable<Block> getKnownBlocks() {
        return knownBlocks;
    }
}