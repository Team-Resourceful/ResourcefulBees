package com.teamresourceful.resourcefulbees.datagen.bases;

import com.google.common.base.Preconditions;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.Lazy;

public abstract class BaseBlockModelProvider extends BlockModelProvider {

    public final Lazy<ModelFile> EMPTY_MODEL = Lazy.of(() -> getExistingFile(mcLoc("air")));

    protected BaseBlockModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), ModConstants.MOD_ID, existingFileHelper);
    }

    @Override
    public BlockModelBuilder getBuilder(String path) {
        Preconditions.checkNotNull(path, "Path must not be null");
        ResourceLocation outputLoc = extendWithFolder(path.contains(":") ? new ResourceLocation(path) : new ResourceLocation(modid, path));
        this.existingFileHelper.trackGenerated(outputLoc, ModelProvider.MODEL);
        return generatedModels.computeIfAbsent(outputLoc, factory);
    }

    protected ResourceLocation extendWithFolder(ResourceLocation rl) {
        return new ResourceLocation(rl.getNamespace(), folder + "/" + rl.getPath());
    }
}
