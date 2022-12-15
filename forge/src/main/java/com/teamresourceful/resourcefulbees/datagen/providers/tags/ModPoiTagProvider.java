package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModPOIs;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.PoiTypeTagsProvider;
import net.minecraft.tags.PoiTypeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModPoiTagProvider extends PoiTypeTagsProvider {

    public ModPoiTagProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(PoiTypeTags.BEE_HOME).add(ModPOIs.TIERED_BEEHIVE_POI.get());
        tag(PoiTypeTags.ACQUIRABLE_JOB_SITE).addTag(PoiTypeTags.BEE_HOME);
    }

    @Override
    public @NotNull String getName() {
        return "Resourceful Bees POI Tag Provider";
    }
}
