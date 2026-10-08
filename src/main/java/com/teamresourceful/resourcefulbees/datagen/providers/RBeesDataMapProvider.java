package com.teamresourceful.resourcefulbees.datagen.providers;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Transformable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class RBeesDataMapProvider extends DataMapProvider {

    public RBeesDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.@NonNull Provider provider) {
        var waxables = builder(NeoForgeDataMaps.WAXABLES);

        waxables.add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.OAK_PLANKS), new Waxable(ModBlocks.WAXED_PLANKS.get(), true), true);
        waxables.add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.OAK_FENCE), new Waxable(ModBlocks.WAXED_FENCE.get(), true), true);
        waxables.add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.OAK_SLAB), new Waxable(ModBlocks.WAXED_SLAB.get(), true), true);
        waxables.add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.OAK_STAIRS), new Waxable(ModBlocks.WAXED_STAIRS.get(), true), true);
        waxables.add(BuiltInRegistries.BLOCK.wrapAsHolder(Blocks.OAK_PRESSURE_PLATE), new Waxable(ModBlocks.WAXED_PRESSURE_PLATE.get(), true), true);
        waxables.add(ModBlocks.WAXED_PLANKS.holder(), new Waxable(ModBlocks.TRIMMED_WAXED_PLANKS.get()), true);
        waxables.add(ModBlocks.TRIMMED_WAXED_PLANKS.holder(), new Waxable(ModBlocks.WAXED_MACHINE_BLOCK.get()), true);

        var transformables = builder(NeoForgeDataMaps.TRANSFORMABLES);

        transformables.add(ModBlocks.WAXED_LOG.holder(), Transformable.stripping(ModBlocks.WAXED_LOG.get(), ModBlocks.STRIPPED_WAXED_LOG.get()), false);
    }
}