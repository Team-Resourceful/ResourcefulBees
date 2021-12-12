package com.teamresourceful.resourcefulbees.client.event;

import com.teamresourceful.resourcefulbees.client.color.ColorHandler;
import com.teamresourceful.resourcefulbees.client.gui.screen.*;
import com.teamresourceful.resourcefulbees.client.models.ModelHandler;
import com.teamresourceful.resourcefulbees.client.render.entity.CustomBeeRenderer;
import com.teamresourceful.resourcefulbees.client.render.fluid.FluidRender;
import com.teamresourceful.resourcefulbees.client.render.items.ItemModelPropertiesHandler;
import com.teamresourceful.resourcefulbees.client.render.pet.BeeRewardRender;
import com.teamresourceful.resourcefulbees.client.render.tileentity.RenderEnderBeecon;
import com.teamresourceful.resourcefulbees.client.render.tileentity.RenderHoneyGenerator;
import com.teamresourceful.resourcefulbees.client.render.tileentity.RenderSolidificationChamber;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModContainers;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

public class ClientEventHandlers {

    //TODO some methods here get called from distRunWhenOn's and could probably be merged into the FMLClientSetupEvent instead
    private ClientEventHandlers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static boolean setupsDone = false;

    public static void clientStuff() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::registerModels);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::onModelBake);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onBlockColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::addLayers);
        MinecraftForge.EVENT_BUS.addListener(FluidRender::honeyOverlay);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::recipesLoaded);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::onTagsUpdated);

    }

    public static void recipesLoaded(RecipesUpdatedEvent event){
        if (Minecraft.getInstance().isLocalServer()) {
            startSetups();
        }
    }

    public static void onTagsUpdated(TagsUpdatedEvent event){
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

    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        PlayerRenderer defaultRenderer = event.getSkin("default");
        PlayerRenderer slimRenderer = event.getSkin("slim");
        if (defaultRenderer != null && slimRenderer != null) {
            defaultRenderer.addLayer(new BeeRewardRender(defaultRenderer));
            slimRenderer.addLayer(new BeeRewardRender(slimRenderer));
        }
    }

    private static void clientSetup(final FMLClientSetupEvent event) {
        ModEntities.getModBees().forEach((s, entityType) ->
                EntityRenderers.register(entityType,
                        manager -> new CustomBeeRenderer<>(manager, BeeRegistry.getRegistry().getBeeData(s).getRenderData())));

        registerScreens();
        registerRenderTypes();

        ItemModelPropertiesHandler.registerProperties();
        registerTERs();
        event.enqueueWork(FluidRender::setHoneyRenderType);
    }

    private static void registerTERs() {
        BlockEntityRenderers.register(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), RenderHoneyGenerator::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY.get(), RenderSolidificationChamber::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), RenderEnderBeecon::new);
    }

    private static void registerRenderTypes() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOLD_FLOWER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PREVIEW_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ERRORED_PREVIEW_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ENDER_BEECON.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SOLIDIFICATION_CHAMBER.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.HONEY_GENERATOR.get(), RenderType.translucent());

        // bee nests need cutout for overlay
        ModBlocks.HIVES.getEntries().stream()
                .filter(RegistryObject::isPresent)
                .map(RegistryObject::get)
                .forEach(nest -> ItemBlockRenderTypes.setRenderLayer(nest, RenderType.cutout()));
    }

    private static void registerScreens() {
        MenuScreens.register(ModContainers.VALIDATED_APIARY_CONTAINER.get(), ValidatedApiaryScreen::new);
        MenuScreens.register(ModContainers.APIARY_BREEDER_CONTAINER.get(), ApiaryBreederScreen::new);
        MenuScreens.register(ModContainers.HONEY_GENERATOR_CONTAINER.get(), HoneyGeneratorScreen::new);
        MenuScreens.register(ModContainers.ENDER_BEECON_CONTAINER.get(), EnderBeeconScreen::new);
        MenuScreens.register(ModContainers.HONEY_CONGEALER_CONTAINER.get(), SolidificationChamberScreen::new);
        MenuScreens.register(ModContainers.HONEY_POT_CONTAINER.get(), HoneyPotScreen::new);

        /* TODO REMOVE TO WORK ON CENTRIFUGE
        //centrifuge
        MenuScreens.register(ModContainers.CENTRIFUGE_INPUT_CONTAINER.get(), CentrifugeInputScreen::new);
        MenuScreens.register(ModContainers.CENTRIFUGE_ITEM_OUTPUT_CONTAINER.get(), CentrifugeItemOutputScreen::new);
        MenuScreens.register(ModContainers.CENTRIFUGE_VOID_CONTAINER.get(), CentrifugeVoidScreen::new);
        MenuScreens.register(ModContainers.CENTRIFUGE_TERMINAL_CONTAINER.get(), CentrifugeTerminalScreen::new);
         */
    }
}
