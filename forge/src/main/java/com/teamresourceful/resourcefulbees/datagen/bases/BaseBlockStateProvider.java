package com.teamresourceful.resourcefulbees.datagen.bases;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.api.RegistryEntry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;

public abstract class BaseBlockStateProvider extends BlockStateProvider {

    protected final BaseBlockModelProvider blockModelProvider;
    protected final BaseItemModelProvider itemModelProvider;


    protected BaseBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ResourcefulBees.MOD_ID, exFileHelper);
        this.blockModelProvider = new BaseBlockModelProvider(gen, exFileHelper) { @Override protected void registerModels() {} };
        this.itemModelProvider = new BaseItemModelProvider(gen, exFileHelper) { @Override protected void registerModels() {} };
    }

    protected String id(RegistryEntry<? extends Block> block) {
        return block.getId().getPath();
    }

    protected String idFull(RegistryEntry<? extends Block> block) {
        return block.getId().toString();
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

    protected void simpleBlockWithItem(Block block, ModelFile model) {
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    protected void horizontalCentrifuge(RegistryEntry<Block> block) {
        ResourceLocation topAndSide = blockTexture(ModBlocks.CENTRIFUGE_CASING.get());
        ModelFile model = models().orientable(id(block), topAndSide, blockTexture(block.get()), topAndSide);
        horizontalBlockWithItem(block.get(), model);
    }

    protected void horizontalCentrifugeBottom(RegistryEntry<Block> block) {
        ModelFile model = models().orientableWithBottom(id(block), blockTexture(ModBlocks.CENTRIFUGE_CASING.get()), blockTexture(block.get()), blockTexture(block.get()), blockTexture(ModBlocks.CENTRIFUGE_CASING.get()));
        horizontalBlockWithItem(block.get(), model);
    }

    protected void horizontalBlockWithItem(Block block, ModelFile model) {
        this.horizontalBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    protected void orientableVerticalWithItem(RegistryEntry<Block> block) {
        ModelFile model = models().orientableVertical(id(block), blockTexture(ModBlocks.CENTRIFUGE_CASING.get()), blockTexture(block.get()));
        this.simpleBlock(block.get(), model);
        this.simpleBlockItem(block.get(), model);
    }

    protected void stairBlockWithItem(RegistryEntry<? extends StairBlock> block, ResourceLocation texture) {
        String baseName = idFull(block) + "_stair";
        ModelFile stairs = models().stairs(baseName, texture, texture, texture);
        ModelFile stairsInner = models().stairsInner(baseName + "_inner", texture, texture, texture);
        ModelFile stairsOuter = models().stairsOuter(baseName + "_outer", texture, texture, texture);
        stairsBlock(block.get(), stairs, stairsInner, stairsOuter);
        this.simpleBlockItem(block.get(), stairs);
    }

    protected void trapdoorBlockWithItem(RegistryEntry<? extends TrapDoorBlock> block, ResourceLocation texture) {
        String baseName = idFull(block) + "_trapdoor";
        ModelFile bottom = models().trapdoorOrientableBottom(baseName + "_bottom", texture).renderType("cutcout");
        ModelFile top = models().trapdoorOrientableTop(baseName + "_top", texture).renderType("cutcout");
        ModelFile open = models().trapdoorOrientableOpen(baseName + "_open", texture).renderType("cutcout");
        trapdoorBlock(block.get(), bottom, top, open, true);
        this.simpleBlockItem(block.get(), bottom);
    }

    protected void buttonBlockWithItem(RegistryEntry<? extends ButtonBlock> block, ResourceLocation texture) {
        this.buttonBlock(block.get(), texture);
        this.simpleBlockItem(block.get(), models().buttonInventory(id(block) + "_inventory", texture));
    }

    protected void fenceBlockWithItem(RegistryEntry<? extends FenceBlock> block, ResourceLocation texture) {
        this.fenceBlock(block.get(), texture);
        this.simpleBlockItem(block.get(), models().fenceInventory(idFull(block) + "_inventory", texture));
    }

    protected void fenceGateBlockWithItem(RegistryEntry<? extends FenceGateBlock> block, ResourceLocation texture) {
        String baseName = idFull(block) + "_fence_gate";
        ModelFile gate = models().fenceGate(baseName, texture);
        ModelFile gateOpen = models().fenceGateOpen(baseName + "_open", texture);
        ModelFile gateWall = models().fenceGateWall(baseName + "_wall", texture);
        ModelFile gateWallOpen = models().fenceGateWallOpen(baseName + "_wall_open", texture);
        fenceGateBlock(block.get(), gate, gateOpen, gateWall, gateWallOpen);
        this.simpleBlockItem(block.get(), gate);
    }

    protected void preasurePlateBlockWithItem(RegistryEntry<PressurePlateBlock> block, ResourceLocation texture) {
        ModelFile pressurePlate = models().pressurePlate(id(block), texture);
        ModelFile pressurePlateDown = models().pressurePlateDown(id(block) + "_down", texture);
        pressurePlateBlock(block.get(), pressurePlate, pressurePlateDown);
        this.simpleBlockItem(block.get(), pressurePlate);
    }

    protected void slabBlockWithItem(RegistryEntry<SlabBlock> block, ResourceLocation texture) {
        BlockModelBuilder model = models().slab(id(block), texture, texture, texture);
        slabBlock(block.get(), model, models().slabTop(id(block) + "_top", texture, texture, texture), models().getExistingFile(texture));
        this.simpleBlockItem(block.get(), model);
    }
}
