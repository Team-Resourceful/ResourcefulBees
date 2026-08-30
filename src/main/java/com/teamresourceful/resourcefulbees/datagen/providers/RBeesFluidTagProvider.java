package com.teamresourceful.resourcefulbees.datagen.providers;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModFluids;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class RBeesFluidTagProvider extends FluidTagsProvider {
    public RBeesFluidTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, ModConstants.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        tag(ModFluidTags.HONEY).add(ResourceKey.create(BuiltInRegistries.FLUID.key(), ModFluids.HONEY_STILL.getId()));
    }

    @Override
    public @NonNull String getName() {
        return "Resourceful Bees Fluid Tag Provider";
    }
}
