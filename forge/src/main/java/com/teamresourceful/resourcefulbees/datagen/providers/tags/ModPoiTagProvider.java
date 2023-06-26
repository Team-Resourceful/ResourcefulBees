package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModPOIs;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PoiTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModPoiTagProvider extends PoiTypeTagsProvider {

    public ModPoiTagProvider(DataGenerator pGenerator, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator.getPackOutput(), provider, ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(PoiTypeTags.BEE_HOME).add(ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, ModPOIs.TIERED_BEEHIVE_POI.getId()));
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).addTag(PoiTypeTags.BEE_HOME);
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees POI Tag Provider";
    }
}
