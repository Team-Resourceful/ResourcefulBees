package com.resourcefulbees.resourcefulbees.client.models;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.JsonOps;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.lib.HoneycombTypes;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.utils.BeeInfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
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
    private static final String HONEYCOMB_BLOCK = "_honeycomb_block";


    private ModelHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    //region Bee Model Registries
    private static void registerHoneycombBlockState(String s, ResourceManager resourceManager){
        Block honeycombBlock = BeeInfoUtils.getBlock(ResourcefulBees.MOD_ID + ":" + s + HONEYCOMB_BLOCK);
        if (honeycombBlock != null && honeycombBlock.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, "blockstates/" + honeycombBlock.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            honeycombBlock.getStateDefinition().getPossibleStates().forEach(state -> {
                String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
                ModelResourceLocation defaultModelLocation = new ModelResourceLocation(ResourcefulBees.MOD_ID + ":honeycomb_block", propertyMapString);
                ModelLoader.addSpecialModel(defaultModelLocation);
                MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeycombBlock.getRegistryName(), propertyMapString));
            });
        }
    }

    private static void registerHoneycombBlockItem(String s, ResourceManager resourceManager){
        Item honeycombBlockItem = BeeInfoUtils.getItem(ResourcefulBees.MOD_ID + ":" + s + HONEYCOMB_BLOCK);/// <<- create utility methods to get these objects from the bee type.
        if (honeycombBlockItem != null && honeycombBlockItem.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeycombBlockItem.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(ResourcefulBees.MOD_ID + ":honeycomb_block", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeycombBlockItem.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerHoneycombItem(String s, ResourceManager resourceManager){
        Item honeycomb = BeeInfoUtils.getItem(ResourcefulBees.MOD_ID + ":" + s + HONEYCOMB_BLOCK);/// <<- create utility methods to get these objects from the bee type.
        if (honeycomb != null && honeycomb.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeycomb.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(ResourcefulBees.MOD_ID + ":honeycomb", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeycomb.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerBeeSpawnEgg(String s, ResourceManager resourceManager){
        Item spawnEgg = BeeInfoUtils.getItem(ResourcefulBees.MOD_ID + ":" + s + "_bee_spawn_egg");  /// <<- create utility methods to get these objects from the bee type.
        if (spawnEgg != null && spawnEgg.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + spawnEgg.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
                    "minecraft:template_spawn_egg", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(spawnEgg.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    //endregion

    //region Honey Model Registries
    private static void registerHoneyBottleItem(@NotNull HoneyBottleData honeyBottleData, ResourceManager resourceManager){
        Item honeyBottleItem = honeyBottleData.getHoneyBottleRegistryObject() != null ? honeyBottleData.getHoneyBottleRegistryObject().get() : null;
        if (honeyBottleItem != null && honeyBottleItem.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeyBottleItem.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(ResourcefulBees.MOD_ID + ":honey_bottle", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeyBottleItem.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerHoneyBlockItem(@NotNull HoneyBottleData honeyBottleData, ResourceManager resourceManager){
        Item honeyBlockItem = honeyBottleData.getHoneyBlockItemRegistryObject() != null ? honeyBottleData.getHoneyBlockItemRegistryObject().get() : null;
        if (honeyBlockItem != null && honeyBlockItem.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeyBlockItem.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
                    ResourcefulBees.MOD_ID + ":honey_block", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeyBlockItem.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerHoneyBucketItem(@NotNull HoneyBottleData honeyBottleData, ResourceManager resourceManager){
        Item honeyBucketItem = honeyBottleData.getHoneyBucketItemRegistryObject() != null ? honeyBottleData.getHoneyBucketItemRegistryObject().get() : null;
        if (honeyBucketItem != null && honeyBucketItem.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, ITEM_MODEL_PATH + honeyBucketItem.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
                    ResourcefulBees.MOD_ID + ":custom_honey_fluid_bucket", MODEL_INVENTORY_TAG);
            ModelLoader.addSpecialModel(defaultModelLocation);
            MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeyBucketItem.getRegistryName(), MODEL_INVENTORY_TAG));
        }
    }

    private static void registerHoneyBlock(@NotNull HoneyBottleData honeyBottleData, ResourceManager resourceManager){
        Block honeyBlock = honeyBottleData.getHoneyBlockRegistryObject() != null ? honeyBottleData.getHoneyBlockRegistryObject().get() : null;
        if (honeyBlock != null) {
            ItemBlockRenderTypes.setRenderLayer(honeyBlock, RenderType.translucent());
            if (honeyBlock.getRegistryName() != null && !resourceManager.hasResource(new ResourceLocation(ResourcefulBees.MOD_ID, "blockstates/" + honeyBlock.getRegistryName().getPath() + JSON_FILE_EXTENSION))) {
                honeyBlock.getStateDefinition().getPossibleStates().forEach(state -> {
                    String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
                    ModelResourceLocation defaultModelLocation = new ModelResourceLocation(
                            ResourcefulBees.MOD_ID + ":honey_block", propertyMapString);
                    ModelLoader.addSpecialModel(defaultModelLocation);
                    MODEL_MAP.put(defaultModelLocation, new ModelResourceLocation(honeyBlock.getRegistryName(), propertyMapString));
                });
            }
        }
    }

    private static void registerHoneyFluid(@NotNull HoneyBottleData honeyBottleData){
        LiquidBlock honeyFluidBlock = honeyBottleData.getHoneyFluidBlockRegistryObject() != null ? honeyBottleData.getHoneyFluidBlockRegistryObject().get() : null;
        FlowingFluid honeyStillFluid = honeyBottleData.getHoneyStillFluidRegistryObject() != null ? honeyBottleData.getHoneyStillFluidRegistryObject().get() : null;
        FlowingFluid honeyFlowingFluid = honeyBottleData.getHoneyFlowingFluidRegistryObject() != null ? honeyBottleData.getHoneyFlowingFluidRegistryObject().get() : null;

        if (honeyStillFluid != null) {
            ItemBlockRenderTypes.setRenderLayer(honeyStillFluid, RenderType.translucent());
        }
        if (honeyFlowingFluid != null) {
            ItemBlockRenderTypes.setRenderLayer(honeyFlowingFluid, RenderType.translucent());
        }
        if (honeyFluidBlock != null) {
            ItemBlockRenderTypes.setRenderLayer(honeyFluidBlock, RenderType.translucent());
        }
    }
    //endregion


    public static void registerModels(ModelRegistryEvent event) {
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        BeeRegistry.getRegistry().getRawBees().forEach((s, beeData) -> {
            HoneycombTypes honeycombType = HoneycombTypes.CODEC.fieldOf("honeycombType").orElse(HoneycombTypes.DEFAULT).codec().fieldOf("HoneycombData").codec().parse(JsonOps.INSTANCE, beeData).get().orThrow();
                if (honeycombType.equals(HoneycombTypes.DEFAULT)) {
                    registerHoneycombBlockState(s, resourceManager);
                    registerHoneycombBlockItem(s, resourceManager);
                    registerHoneycombItem(s, resourceManager);
                }
                registerBeeSpawnEgg(s, resourceManager);
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
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
        BakedModel missingModel = modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
        MODEL_MAP.asMap().forEach(((resourceLocation, resourceLocations) -> {
            BakedModel defaultModel = modelRegistry.getOrDefault(resourceLocation, missingModel);
            resourceLocations.forEach(modelLocation -> modelRegistry.put(modelLocation, defaultModel));
        }));
    }
}