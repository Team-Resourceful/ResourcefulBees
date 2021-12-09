package com.teamresourceful.resourcefulbees.client.models;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class ModelHandler {

    private static final Multimap<ResourceLocation, ResourceLocation> MODEL_MAP = LinkedHashMultimap.create();
    private static final String MODEL_INVENTORY_TAG = "inventory";
    private static final String ITEM_MODEL_PATH = "item/models/";
    private static final String JSON_FILE_EXTENSION = ".json";

    private ModelHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static void registerGenericBlockState(RegistryObject<Block> block, String parentModel, RenderType renderType, ResourceManager resourceManager) {
        if (!block.isPresent()) return;

        if (renderType != null) ItemBlockRenderTypes.setRenderLayer(block.get(), renderType);
        if (!resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, "blockstates/" + block.getId().getPath() + JSON_FILE_EXTENSION))) {
            block.get().getStateDefinition().getPossibleStates().forEach(state -> {
                String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
                ModelResourceLocation defaultModelLocation = new ModelResourceLocation(parentModel, propertyMapString);
                ForgeModelBakery.addSpecialModel(defaultModelLocation);
                MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(block.getId(), propertyMapString));
            });
        }
    }

    private static void registerGenericItem(RegistryObject<Item> item, String parentModel, ResourceManager resourceManager) {
        if (item.isPresent() && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + item.getId().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(parentModel, MODEL_INVENTORY_TAG);
            ForgeModelBakery.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(item.getId(), MODEL_INVENTORY_TAG));
        }
    }

    @SuppressWarnings("unused")
    public static void registerModels(ModelRegistryEvent event) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        ModItems.HONEYCOMB_ITEMS.getEntries().forEach(comb -> registerGenericItem(comb, ResourcefulBees.MOD_ID + ":honeycomb", resourceManager));
        ModItems.HONEYCOMB_BLOCK_ITEMS.getEntries().forEach(combBlock -> registerGenericItem(combBlock, ResourcefulBees.MOD_ID + ":honeycomb_block", resourceManager));

        ModItems.HONEY_BLOCK_ITEMS.getEntries().forEach(honeyBlock -> registerGenericItem(honeyBlock, ResourcefulBees.MOD_ID + ":honey_block", resourceManager));
        ModItems.HONEY_BUCKET_ITEMS.getEntries().forEach(bucket -> registerGenericItem(bucket, ResourcefulBees.MOD_ID + ":custom_honey_fluid_bucket", resourceManager));
        ModItems.HONEY_BOTTLE_ITEMS.getEntries().forEach(bucket -> registerGenericItem(bucket, ResourcefulBees.MOD_ID + ":honey_bottle", resourceManager));

        ModItems.SPAWN_EGG_ITEMS.getEntries().forEach(egg -> registerGenericItem(egg, "minecraft:template_spawn_egg", resourceManager));

        ModBlocks.HONEYCOMB_BLOCKS.getEntries().forEach(combBlock -> registerGenericBlockState(combBlock, ResourcefulBees.MOD_ID + ":honeycomb_block", null, resourceManager));

        ModBlocks.HONEY_BLOCKS.getEntries().forEach(honeyBlock -> registerGenericBlockState(honeyBlock, ResourcefulBees.MOD_ID + ":honey_block", RenderType.translucent(), resourceManager));

        ModBlocks.HONEY_FLUID_BLOCKS.getEntries().forEach(honeyFluid -> honeyFluid.ifPresent(block -> ItemBlockRenderTypes.setRenderLayer(block, RenderType.translucent())));

    }

    public static void onModelBake(ModelBakeEvent event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
        BakedModel missingModel = modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
        MODEL_MAP.asMap().forEach(((resourceLocation, resourceLocations) -> {
            BakedModel defaultModel = modelRegistry.getOrDefault(resourceLocation, missingModel);
            resourceLocations.forEach(modelLocation ->
                modelRegistry.computeIfPresent(modelLocation, (resourceLocation1, iBakedModel) -> {
                    if (iBakedModel.getParticleIcon(EmptyModelData.INSTANCE) instanceof MissingTextureAtlasSprite) return defaultModel;
                    else return iBakedModel;
                })
            );
        }));
    }
}
