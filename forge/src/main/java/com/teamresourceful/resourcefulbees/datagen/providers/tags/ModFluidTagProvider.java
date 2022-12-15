package com.teamresourceful.resourcefulbees.datagen.providers.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tags.ModFluidTags;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModFluidTagProvider extends FluidTagsProvider {

    public ModFluidTagProvider(DataGenerator pGenerator, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(ModFluidTags.HONEY).add(ModFluids.HONEY_STILL.get());
    }
}
