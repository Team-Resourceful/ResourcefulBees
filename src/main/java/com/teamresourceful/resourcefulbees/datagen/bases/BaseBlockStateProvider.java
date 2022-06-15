package com.teamresourceful.resourcefulbees.datagen.bases;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public abstract class BaseBlockStateProvider extends BlockStateProvider {

    protected final BaseBlockModelProvider blockModelProvider;
    protected final BaseItemModelProvider itemModelProvider;


    protected BaseBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ResourcefulBees.MOD_ID, exFileHelper);
        this.blockModelProvider = new BaseBlockModelProvider(gen, exFileHelper) { @Override protected void registerModels() {} };
        this.itemModelProvider = new BaseItemModelProvider(gen, exFileHelper) { @Override protected void registerModels() {} };
    }

    protected String id(Block block) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();
    }

    protected String idFull(Block block) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).toString();
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

    protected void horizontalCentrifuge(Block block) {
        ResourceLocation topAndSide = blockTexture(ModBlocks.CENTRIFUGE_CASING.get());
        ModelFile model = models().orientable(id(block), topAndSide, blockTexture(block), topAndSide);
        horizontalBlockWithItem(block, model);
    }

    protected void horizontalCentrifugeBottom(Block block) {
        ModelFile model = models().orientableWithBottom(id(block), blockTexture(ModBlocks.CENTRIFUGE_CASING.get()), blockTexture(block), blockTexture(block), blockTexture(ModBlocks.CENTRIFUGE_CASING.get()));
        horizontalBlockWithItem(block, model);
    }

    protected void horizontalBlockWithItem(Block block, ModelFile model) {
        this.horizontalBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    protected void orientableVerticalWithItem(Block block) {
        ModelFile model = models().orientableVertical(id(block), blockTexture(ModBlocks.CENTRIFUGE_CASING.get()), blockTexture(block));
        this.simpleBlock(block, model);
        this.simpleBlockItem(block, model);
    }

    protected void stairBlockWithItem(StairBlock block, ResourceLocation texture) {
        String baseName = idFull(block) + "_stair";
        ModelFile stairs = models().stairs(baseName, texture, texture, texture);
        ModelFile stairsInner = models().stairsInner(baseName + "_inner", texture, texture, texture);
        ModelFile stairsOuter = models().stairsOuter(baseName + "_outer", texture, texture, texture);
        stairsBlock(block, stairs, stairsInner, stairsOuter);
        this.simpleBlockItem(block, stairs);
    }

    protected void trapdoorBlockWithItem(TrapDoorBlock block, ResourceLocation texture) {
        String baseName = idFull(block) + "_trapdoor";
        ModelFile bottom = models().trapdoorOrientableBottom(baseName + "_bottom", texture);
        ModelFile top = models().trapdoorOrientableTop(baseName + "_top", texture);
        ModelFile open = models().trapdoorOrientableOpen(baseName + "_open", texture);
        trapdoorBlock(block, bottom, top, open, true);
        this.simpleBlockItem(block, bottom);
    }

    protected void buttonBlockWithItem(ButtonBlock block, ResourceLocation texture) {
        this.buttonBlock(block, texture);
        this.simpleBlockItem(block, models().buttonInventory(id(block) + "_inventory", texture));
    }

    protected void fenceBlockWithItem(FenceBlock block, ResourceLocation texture) {
        this.fenceBlock(block, texture);
        this.simpleBlockItem(block, models().fenceInventory(idFull(block) + "_inventory", texture));
    }

    protected void fenceGateBlockWithItem(FenceGateBlock block, ResourceLocation texture) {
        String baseName = idFull(block) + "_fence_gate";
        ModelFile gate = models().fenceGate(baseName, texture);
        ModelFile gateOpen = models().fenceGateOpen(baseName + "_open", texture);
        ModelFile gateWall = models().fenceGateWall(baseName + "_wall", texture);
        ModelFile gateWallOpen = models().fenceGateWallOpen(baseName + "_wall_open", texture);
        fenceGateBlock(block, gate, gateOpen, gateWall, gateWallOpen);
        this.simpleBlockItem(block, gate);
    }

    protected void preasurePlateBlockWithItem(PressurePlateBlock block, ResourceLocation texture) {
        ModelFile pressurePlate = models().pressurePlate(id(block), texture);
        ModelFile pressurePlateDown = models().pressurePlateDown(id(block) + "_down", texture);
        pressurePlateBlock(block, pressurePlate, pressurePlateDown);
        this.simpleBlockItem(block, pressurePlate);
    }

    protected void slabBlockWithItem(SlabBlock block, ResourceLocation texture) {
        BlockModelBuilder model = models().slab(id(block), texture, texture, texture);
        slabBlock(block, model, models().slabTop(id(block) + "_top", texture, texture, texture), models().getExistingFile(texture));
        this.simpleBlockItem(block, model);
    }
}
