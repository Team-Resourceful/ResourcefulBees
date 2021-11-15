package com.teamresourceful.resourcefulbees.datagen.bases;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public abstract class BaseBlockStateProvider extends BlockStateProvider {

    protected final BaseBlockModelProvider blockModelProvider;
    protected final BaseItemModelProvider itemModelProvider;


    protected BaseBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ResourcefulBees.MOD_ID, exFileHelper);
        this.blockModelProvider = new BaseBlockModelProvider(gen, exFileHelper) { @Override protected void registerModels() {} };
        this.itemModelProvider = new BaseItemModelProvider(gen, exFileHelper) { @Override protected void registerModels() {} };
    }

    protected String name(Block block) {
        return block.getRegistryName().getPath();
    }

    @Override
    public BlockModelProvider models() {
        return blockModelProvider;
    }

    @Override
    public ItemModelProvider itemModels() {
        return itemModelProvider;
    }

    protected void simpleBlockWithItem(Block block) {
        ModelFile model = cubeAll(block);
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    protected void horizontalCentrifuge(Block block) {
        ResourceLocation topAndSide = blockTexture(ModBlocks.CENTRIFUGE_CASING.get());
        ModelFile model = models().orientable(name(block), topAndSide, blockTexture(block), topAndSide);
        horizontalBlockWithItem(block, model);
    }

    protected void horizontalCentrifugeBottom(Block block) {
        ModelFile model = models().orientableWithBottom(name(block), blockTexture(ModBlocks.CENTRIFUGE_CASING.get()), blockTexture(block), blockTexture(block), blockTexture(ModBlocks.CENTRIFUGE_CASING.get()));
        horizontalBlockWithItem(block, model);
    }

    protected void horizontalBlockWithItem(Block block, ModelFile model) {
        this.horizontalBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    protected void orientableVerticalWithItem(Block block) {
        ModelFile model = models().orientableVertical(name(block), blockTexture(ModBlocks.CENTRIFUGE_CASING.get()), blockTexture(block));
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }
}
