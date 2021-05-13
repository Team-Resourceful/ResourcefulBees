package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.client.gui.screen.*;
import com.resourcefulbees.resourcefulbees.client.models.ModelHandler;
import com.resourcefulbees.resourcefulbees.client.render.entity.CustomBeeRenderer;
import com.resourcefulbees.resourcefulbees.client.render.fluid.FluidRender;
import com.resourcefulbees.resourcefulbees.client.render.items.ItemModelPropertiesHandler;
import com.resourcefulbees.resourcefulbees.client.render.patreon.BeeRewardRender;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderEnderBeecon;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderHoneyCongealer;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderHoneyGenerator;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderHoneyTank;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.*;
import com.resourcefulbees.resourcefulbees.utils.PreviewHandler;
import com.resourcefulbees.resourcefulbees.utils.color.ColorHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandlers {

    private ClientEventHandlers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static boolean setupsDone = false;

    public static void clientStuff() {
        MinecraftForge.EVENT_BUS.addListener(PreviewHandler::onWorldRenderLast);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::registerModels);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::onModelBake);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::doClientStuff);
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
        EntityRenderDispatcher manager =  Minecraft.getInstance().getEntityRenderDispatcher();
        manager.getSkinMap().get("default").addLayer(new BeeRewardRender(manager.defaultPlayerRenderer));
        manager.getSkinMap().get("slim").addLayer(new BeeRewardRender(manager.defaultPlayerRenderer));
    }

    private static void doClientStuff(final FMLClientSetupEvent event) {
        ModEntities.getModBees().forEach((s, customBee) -> RenderingRegistry.registerEntityRenderingHandler(customBee.get(), manager -> new CustomBeeRenderer(manager, BeeRegistry.getRegistry().getBeeData(s).getRenderData())));
        MenuScreens.register(ModContainers.CENTRIFUGE_CONTAINER.get(), CentrifugeScreen::new);
        MenuScreens.register(ModContainers.MECHANICAL_CENTRIFUGE_CONTAINER.get(), MechanicalCentrifugeScreen::new);
        MenuScreens.register(ModContainers.CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), CentrifugeMultiblockScreen::new);
        MenuScreens.register(ModContainers.ELITE_CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), CentrifugeMultiblockScreen::new);
        MenuScreens.register(ModContainers.UNVALIDATED_APIARY_CONTAINER.get(), UnvalidatedApiaryScreen::new);
        MenuScreens.register(ModContainers.VALIDATED_APIARY_CONTAINER.get(), ValidatedApiaryScreen::new);
        MenuScreens.register(ModContainers.APIARY_STORAGE_CONTAINER.get(), ApiaryStorageScreen::new);
        MenuScreens.register(ModContainers.APIARY_BREEDER_CONTAINER.get(), ApiaryBreederScreen::new);
        MenuScreens.register(ModContainers.HONEY_GENERATOR_CONTAINER.get(), HoneyGeneratorScreen::new);
        MenuScreens.register(ModContainers.ENDER_BEECON_CONTAINER.get(), EnderBeeconScreen::new);
        MenuScreens.register(ModContainers.HONEY_CONGEALER_CONTAINER.get(), HoneyCongealerScreen::new);
        MenuScreens.register(ModContainers.HONEY_TANK_CONTAINER.get(), HoneyTankScreen::new);

        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOLD_FLOWER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PREVIEW_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ERRORED_PREVIEW_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ENDER_BEECON.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.HONEY_CONGEALER.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PURPUR_HONEY_TANK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.NETHER_HONEY_TANK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WOODEN_HONEY_TANK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.HONEY_GENERATOR.get(), RenderType.translucent());

        // bee nests need transparency for overlay
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.OAK_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.SPRUCE_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BIRCH_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRIMSON_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.DARK_OAK_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.RED_MUSHROOM_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WARPED_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WARPED_NYLIUM_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.ACACIA_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.GRASS_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.JUNGLE_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.NETHER_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PRISMARINE_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.PURPUR_BEE_NEST.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WITHER_BEE_NEST.get(), RenderType.translucent());

        ItemModelPropertiesHandler.registerProperties();
        ClientRegistry.bindTileEntityRenderer(ModBlockEntityTypes.HONEY_TANK_TILE_ENTITY.get(), RenderHoneyTank::new);
        ClientRegistry.bindTileEntityRenderer(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), RenderHoneyGenerator::new);
        ClientRegistry.bindTileEntityRenderer(ModBlockEntityTypes.HONEY_CONGEALER_TILE_ENTITY.get(), RenderHoneyCongealer::new);
        ClientRegistry.bindTileEntityRenderer(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), RenderEnderBeecon::new);
        event.enqueueWork(FluidRender::setHoneyRenderType);
    }
}
