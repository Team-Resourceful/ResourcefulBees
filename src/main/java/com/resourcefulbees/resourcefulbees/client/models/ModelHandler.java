package com.resourcefulbees.resourcefulbees.client.models;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ModelHandler {

    private static final Multimap<ResourceLocation, ResourceLocation> MODEL_MAP = LinkedHashMultimap.create();
    private static final String MODEL_INVENTORY_TAG = "inventory";
    private static final String ITEM_MODEL_PATH = "item/models/";
    private static final String JSON_FILE_EXTENSION = ".json";


    private ModelHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    //region Bee Model Registries
    private static void registerHoneycombBlockstate(@NotNull CustomBeeData customBeeData, IResourceManager resourceManager){
        Block honeycombBlock = customBeeData.getCombBlockRegistryObject() != null ? customBeeData.getCombBlockRegistryObject().get() : null;
        if (honeycombBlock != null && honeycombBlock.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, "blockstates/" + honeycombBlock.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            honeycombBlock.getStateContainer().getValidStates().forEach(state -> {
                String propertyMapString = BlockModelShapes.getPropertyMapString(state.getValues());
                ModelResourceLocation defaultModelLocation = new ModelResourceLocation(ResourcefulBees.MOD_ID + ":honeycomb_block", propertyMapString);
                ModelLoader.addSpecialModel(defaultModelLocation);
                MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeycombBlock.getRegistryName(), propertyMapString));
            });
        }
    }

    private static void registerHoneycombBlockItem(@NotNull CustomBeeData customBeeData, IResourceManager resourceManager){
        Item honeycombBlockItem = customBeeData.getCombBlockItemRegistryObject() != null ? customBeeData.getCombBlockItemRegistryObject().get() : null;
        if (honeycombBlockItem != null && honeycombBlockItem.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeycombBlockItem.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(ResourcefulBees.MOD_ID + ":honeycomb_block", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeycombBlockItem.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerHoneycombItem(@NotNull CustomBeeData customBeeData, IResourceManager resourceManager){
        Item honeycomb = customBeeData.getCombRegistryObject() != null ? customBeeData.getCombRegistryObject().get() : null;
        if (honeycomb != null && honeycomb.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeycomb.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(ResourcefulBees.MOD_ID + ":honeycomb", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeycomb.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerBeeSpawnegg(@NotNull CustomBeeData customBeeData, IResourceManager resourceManager){
        Item spawnEgg = customBeeData.getSpawnEggItemRegistryObject() != null ? customBeeData.getSpawnEggItemRegistryObject().get() : null;
        if (spawnEgg != null && spawnEgg.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + spawnEgg.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
                    "minecraft:template_spawn_egg", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(spawnEgg.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    //endregion

    //region Honey Model Registries
    private static void registerHoneyBottleItem(@NotNull HoneyBottleData honeyBottleData, IResourceManager resourceManager){
        Item honeyBottleItem = honeyBottleData.getHoneyBottleRegistryObject() != null ? honeyBottleData.getHoneyBottleRegistryObject().get() : null;
        if (honeyBottleItem != null && honeyBottleItem.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeyBottleItem.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(ResourcefulBees.MOD_ID + ":honey_bottle", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeyBottleItem.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerHoneyBlockItem(@NotNull HoneyBottleData honeyBottleData, IResourceManager resourceManager){
        Item honeyBlockItem = honeyBottleData.getHoneyBlockItemRegistryObject() != null ? honeyBottleData.getHoneyBlockItemRegistryObject().get() : null;
        if (honeyBlockItem != null && honeyBlockItem.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeyBlockItem.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
                    ResourcefulBees.MOD_ID + ":honey_block", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeyBlockItem.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerHoneyBucketItem(@NotNull HoneyBottleData honeyBottleData, IResourceManager resourceManager){
        Item honeyBucketItem = honeyBottleData.getHoneyBucketItemRegistryObject() != null ? honeyBottleData.getHoneyBucketItemRegistryObject().get() : null;
        if (honeyBucketItem != null && honeyBucketItem.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeyBucketItem.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
                    ResourcefulBees.MOD_ID + ":custom_honey_fluid_bucket", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeyBucketItem.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerHoneyBlock(@NotNull HoneyBottleData honeyBottleData, IResourceManager resourceManager){
        Block honeyBlock = honeyBottleData.getHoneyBlockRegistryObject() != null ? honeyBottleData.getHoneyBlockRegistryObject().get() : null;
        if (honeyBlock != null) {
            RenderTypeLookup.setRenderLayer(honeyBlock, RenderType.getTranslucent());
            if (honeyBlock.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, "blockstates/" + honeyBlock.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
                honeyBlock.getStateContainer().getValidStates().forEach(state -> {
                    String propertyMapString = BlockModelShapes.getPropertyMapString(state.getValues());
                    ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
                            ResourcefulBees.MOD_ID + ":honey_block", propertyMapString);
                    ModelLoader.addSpecialModel(defaultModelLocation);
                    MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeyBlock.getRegistryName(), propertyMapString));
                });
            }
        }
    }

    private static void registerHoneyFluid(@NotNull HoneyBottleData honeyBottleData){
        FlowingFluidBlock honeyFluidBlock = honeyBottleData.getHoneyFluidBlockRegistryObject() != null ? honeyBottleData.getHoneyFluidBlockRegistryObject().get() : null;
        FlowingFluid honeyStillFluid = honeyBottleData.getHoneyStillFluidRegistryObject() != null ? honeyBottleData.getHoneyStillFluidRegistryObject().get() : null;
        FlowingFluid honeyFlowingFluid = honeyBottleData.getHoneyFlowingFluidRegistryObject() != null ? honeyBottleData.getHoneyFlowingFluidRegistryObject().get() : null;

        if (honeyStillFluid != null) {
            RenderTypeLookup.setRenderLayer(honeyStillFluid, RenderType.getTranslucent());
        }
        if (honeyFlowingFluid != null) {
            RenderTypeLookup.setRenderLayer(honeyFlowingFluid, RenderType.getTranslucent());
        }
        if (honeyFluidBlock != null) {
            RenderTypeLookup.setRenderLayer(honeyFluidBlock, RenderType.getTranslucent());
        }
    }
    //endregion


    public static void registerModels(ModelRegistryEvent event) {
        IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        BeeRegistry.getRegistry().getBees().forEach((string, customBee) -> {
            if (customBee.shouldResourcefulBeesDoForgeRegistration) {

                if (customBee.hasHoneycomb() && !customBee.hasCustomDrop()) {
                    registerHoneycombBlockstate(customBee, resourceManager);
                    registerHoneycombBlockItem(customBee, resourceManager);
                    registerHoneycombItem(customBee, resourceManager);
                }
                registerBeeSpawnegg(customBee, resourceManager);
            }
        });

        BeeRegistry.getRegistry().getHoneyBottles().forEach((string, honeyData) -> {
            if (honeyData.shouldResourcefulBeesDoForgeRegistration()) {
                registerHoneyFluid(honeyData);


                registerHoneyBottleItem(honeyData, resourceManager);
                registerHoneyBlockItem(honeyData, resourceManager);
                registerHoneyBucketItem(honeyData,resourceManager);
                registerHoneyBlock(honeyData,resourceManager);
            }
        });
    }

    public static void onModelBake(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        IBakedModel missingModel = modelRegistry.get(ModelBakery.MODEL_MISSING);
        MODEL_MAP.asMap().forEach(((resourceLocation, resourceLocations) -> {
            IBakedModel defaultModel = modelRegistry.getOrDefault(resourceLocation, missingModel);
            resourceLocations.forEach(modelLocation -> modelRegistry.put(modelLocation, defaultModel));
        }));
    }
}