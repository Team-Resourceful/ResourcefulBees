//package com.teamresourceful.resourcefulbees.client.data;
//
//import com.google.common.collect.LinkedHashMultimap;
//import com.google.common.collect.Multimap;
//import com.teamresourceful.resourcefulbees.client.events.ModelBakingCompletedEvent;
//import com.teamresourceful.resourcefulbees.client.events.ModelModifyResultEvent;
//import com.teamresourceful.resourcefulbees.client.events.RegisterAdditionalModelsEvent;
//import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
//import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
//import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
//import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModItems;
//import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
//import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.block.BlockModelShaper;
//import net.minecraft.client.renderer.rendertype.RenderType;
//import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
//import net.minecraft.client.renderer.texture.TextureAtlasSprite;
//import net.minecraft.client.resources.model.BakedModel;
//import net.minecraft.client.resources.model.ModelBakery;
//import net.minecraft.resources.Identifier;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.packs.resources.ResourceManager;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.block.Block;
//
//import java.util.Map;
//
//public final class ModelHandler {
//
//    private static final Multimap<Identifier, Identifier> MODEL_MAP = LinkedHashMultimap.create();
//    private static final String MODEL_INVENTORY_TAG = "inventory";
//    private static final String ITEM_MODEL_PATH = "item/models/";
//    private static final String JSON_FILE_EXTENSION = ".json";
//
//    private ModelHandler() throws UtilityClassException {
//        throw new UtilityClassException();
//    }
//
//    private static void registerGenericBlockState(RegisterAdditionalModelsEvent event, RegistryEntry<Block> block, Identifier parentModel, RenderType renderType, ResourceManager resourceManager) {
//        if (resourceManager.getResource(Identifier.fromNamespaceAndPath(ModConstants.MOD_ID, "blockstates/" + block.getId().getPath() + JSON_FILE_EXTENSION)).isEmpty()) {
//            block.get().getStateDefinition().getPossibleStates().forEach(state -> {
//                String propertyMapString = BlockModelShaper.statePropertiesToString(state.getValues());
//                ModelResourceLocation defaultModelLocation = new ModelResourceLocation(parentModel, propertyMapString);
//                event.register(defaultModelLocation.id());
//                MODEL_MAP.put(defaultModelLocation.id(), new ModelResourceLocation(block.getId(), propertyMapString).id());
//            });
//        }
//    }
//
//    private static void registerGenericItem(RegisterAdditionalModelsEvent event, RegistryEntry<Item> item, Identifier parentModel, ResourceManager resourceManager) {
//        if (resourceManager.getResource(ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, ITEM_MODEL_PATH + item.getId().getPath() + JSON_FILE_EXTENSION)).isEmpty()) {
//            ModelResourceLocation defaultModelLocation = new ModelResourceLocation(parentModel, MODEL_INVENTORY_TAG);
//            event.register(defaultModelLocation.id());
//            MODEL_MAP.put(defaultModelLocation.id(), new ModelResourceLocation(item.getId(), MODEL_INVENTORY_TAG).id());
//        }
//    }
//
//    public static void onAddAdditional(RegisterAdditionalModelsEvent event) {
//        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
//
//        ModItems.HONEYCOMB_ITEMS.getEntries().forEach(comb -> registerGenericItem(event, comb, ModIdentifier.of("honeycomb"), resourceManager));
//        ModItems.HONEYCOMB_BLOCK_ITEMS.getEntries().forEach(combBlock -> registerGenericItem(event, combBlock, ModIdentifier.of("honeycomb_block"), resourceManager));
//
//        ModItems.HONEY_BLOCK_ITEMS.getEntries().forEach(honeyBlock -> registerGenericItem(event, honeyBlock, ModIdentifier.of("honey_block"), resourceManager));
//        ModItems.HONEY_BUCKET_ITEMS.getEntries().forEach(bucket -> registerGenericItem(event, bucket, ModIdentifier.of("custom_honey_fluid_bucket"), resourceManager));
//        ModItems.HONEY_BOTTLE_ITEMS.getEntries().forEach(bucket -> registerGenericItem(event, bucket, ModIdentifier.of("honey_bottle"), resourceManager));
//
//        ModItems.SPAWN_EGG_ITEMS.getEntries().forEach(egg -> registerGenericItem(event, egg,  Identifier.tryParse("minecraft:template_spawn_egg"), resourceManager));
//
//        ModBlocks.HONEYCOMB_BLOCKS.getEntries().forEach(combBlock -> registerGenericBlockState(event, combBlock, ModIdentifier.of("honeycomb_block"), null, resourceManager));
//
//        ModBlocks.HONEY_BLOCKS.getEntries().forEach(honeyBlock -> registerGenericBlockState(event, honeyBlock, ModIdentifier.of("honey_block"), RenderType.translucent(), resourceManager));
//    }
//
//    public static void onModifyModel(ModelModifyResultEvent event) {
//        Map<Identifier, BakedModel> modelRegistry = event.models();
//        BakedModel missingModel = modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
//        MODEL_MAP.asMap().forEach(((resourceLocation, resourceLocations) -> {
//            BakedModel defaultModel = modelRegistry.getOrDefault(resourceLocation, missingModel);
//            resourceLocations.forEach(modelLocation ->
//                    modelRegistry.computeIfPresent(modelLocation, (resourceLocation1, iBakedModel) -> {
//                        TextureAtlasSprite sprite = iBakedModel.getParticleIcon();
//                        if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) {
//                            return defaultModel;
//                        }
//                        return iBakedModel;
//                    })
//            );
//        }));
//    }
//
//    public static void onModelBake(ModelBakingCompletedEvent event) {
//        ClientRenderUtils.DEFAULT_TEXTURER.clear();
//    }
//}
