package com.teamresourceful.resourcefulbees.client.event;

import com.teamresourceful.resourcefulbees.client.color.ColorHandler;
import com.teamresourceful.resourcefulbees.client.gui.screen.*;
import com.teamresourceful.resourcefulbees.client.models.ModelHandler;
import com.teamresourceful.resourcefulbees.client.render.entity.CustomBeeRenderer;
import com.teamresourceful.resourcefulbees.client.render.fluid.FluidRender;
import com.teamresourceful.resourcefulbees.client.render.items.ItemModelPropertiesHandler;
import com.teamresourceful.resourcefulbees.client.render.pet.BeeRewardRender;
import com.teamresourceful.resourcefulbees.client.render.tileentity.RenderEnderBeecon;
import com.teamresourceful.resourcefulbees.client.render.tileentity.RenderHoneyCongealer;
import com.teamresourceful.resourcefulbees.client.render.tileentity.RenderHoneyGenerator;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.utils.PreviewHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandlers {

    //TODO some methods here get called from distRunWhenOn's and could probably be merged into the FMLClientSetupEvent instead
    private ClientEventHandlers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static boolean setupsDone = false;

    public static void clientStuff() {
        MinecraftForge.EVENT_BUS.addListener(PreviewHandler::onWorldRenderLast);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::registerModels);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::onModelBake);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onBlockColors);
        MinecraftForge.EVENT_BUS.addListener(FluidRender::honeyOverlay);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::recipesLoaded);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::onTagsUpdated);

    }

    public static void recipesLoaded(RecipesUpdatedEvent event){
        if (Minecraft.getInstance().isLocalServer()) {
            startSetups();
        }
    }

    public static void onTagsUpdated(TagsUpdatedEvent.CustomTagTypes event){
        if (!Minecraft.getInstance().isLocalServer()) {
            startSetups();
        }
    }

    private static void startSetups() {
        if (!setupsDone) {
            setupsDone = true;
            BeeRegistry.getRegistry().regenerateCustomBeeData();
        }
    }

    public static void registerPatreonRender() {
        EntityRendererManager manager =  Minecraft.getInstance().getEntityRenderDispatcher();
        PlayerRenderer defaultRenderer = manager.getSkinMap().get("default");
        defaultRenderer.addLayer(new BeeRewardRender(defaultRenderer));
        manager.getSkinMap().get("slim").addLayer(new BeeRewardRender(defaultRenderer));
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        ModEntities.getModBees().forEach((s, entityType) ->
                RenderingRegistry.registerEntityRenderingHandler(entityType,
                        manager -> new CustomBeeRenderer<>(manager, BeeRegistry.getRegistry().getBeeData(s).getRenderData())));

        registerScreens();
        registerRenderTypes();

        ItemModelPropertiesHandler.registerProperties();
        registerTERs();
        event.enqueueWork(FluidRender::setHoneyRenderType);
    }

    private static void registerTERs() {
        ClientRegistry.bindTileEntityRenderer(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), RenderHoneyGenerator::new);
        ClientRegistry.bindTileEntityRenderer(ModBlockEntityTypes.HONEY_CONGEALER_TILE_ENTITY.get(), RenderHoneyCongealer::new);
        ClientRegistry.bindTileEntityRenderer(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), RenderEnderBeecon::new);
    }

    private static void registerRenderTypes() {
        RenderTypeLookup.setRenderLayer(ModBlocks.GOLD_FLOWER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.PREVIEW_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ERRORED_PREVIEW_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ENDER_BEECON.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.HONEY_CONGEALER.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.HONEY_GENERATOR.get(), RenderType.translucent());

        // bee nests need cutout for overlay
        ModBlocks.NEST_BLOCKS.getEntries().stream()
                .filter(RegistryObject::isPresent)
                .map(RegistryObject::get)
                .forEach(nest -> RenderTypeLookup.setRenderLayer(nest, RenderType.cutout()));
    }

    private static void registerScreens() {
        ScreenManager.register(ModContainers.UNVALIDATED_APIARY_CONTAINER.get(), UnvalidatedApiaryScreen::new);
        ScreenManager.register(ModContainers.VALIDATED_APIARY_CONTAINER.get(), ValidatedApiaryScreen::new);
        ScreenManager.register(ModContainers.APIARY_STORAGE_CONTAINER.get(), ApiaryStorageScreen::new);
        ScreenManager.register(ModContainers.APIARY_BREEDER_CONTAINER.get(), ApiaryBreederScreen::new);
        ScreenManager.register(ModContainers.HONEY_GENERATOR_CONTAINER.get(), HoneyGeneratorScreen::new);
        ScreenManager.register(ModContainers.ENDER_BEECON_CONTAINER.get(), EnderBeeconScreen::new);
        ScreenManager.register(ModContainers.HONEY_CONGEALER_CONTAINER.get(), HoneyCongealerScreen::new);
        ScreenManager.register(ModContainers.HONEY_POT_CONTAINER.get(), HoneyPotScreen::new);

        ScreenManager.register(ModContainers.CENTRIFUGE_INPUT_CONTAINER.get(), CentrifugeInputScreen::new);
        ScreenManager.register(ModContainers.CENTRIFUGE_ITEM_OUTPUT_CONTAINER.get(), CentrifugeItemOutputScreen::new);
        ScreenManager.register(ModContainers.CENTRIFUGE_VOID_CONTAINER.get(), CentrifugeVoidScreen::new);
    }
}

