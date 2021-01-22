package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.block.EnderBeecon;
import com.resourcefulbees.resourcefulbees.client.gui.screen.*;
import com.resourcefulbees.resourcefulbees.client.models.ModelHandler;
import com.resourcefulbees.resourcefulbees.client.render.entity.CustomBeeRenderer;
import com.resourcefulbees.resourcefulbees.client.render.fluid.FluidRender;
import com.resourcefulbees.resourcefulbees.client.render.items.ItemModelPropertiesHandler;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderEnderBeecon;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderHoneyGenerator;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderHoneyTank;
import com.resourcefulbees.resourcefulbees.registry.BeeRegistry;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModContainers;
import com.resourcefulbees.resourcefulbees.registry.ModTileEntityTypes;
import com.resourcefulbees.resourcefulbees.utils.PreviewHandler;
import com.resourcefulbees.resourcefulbees.utils.color.ColorHandler;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandlers {

    public static void clientStuff() {
        MinecraftForge.EVENT_BUS.addListener(PreviewHandler::onWorldRenderLast);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::registerModels);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::onModelBake);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onBlockColors);
        MinecraftForge.EVENT_BUS.addListener(FluidRender::honeyOverlay);
    }

    private static void doClientStuff(final FMLClientSetupEvent event) {
        BeeRegistry.MOD_BEES.forEach((s, customBee) -> RenderingRegistry.registerEntityRenderingHandler(customBee.get(), manager -> {
            CustomBeeData data = BeeRegistry.getRegistry().getBeeData(s);
            return new CustomBeeRenderer(data.getBaseModelType(), manager, data);
        }));
        ScreenManager.registerFactory(ModContainers.CENTRIFUGE_CONTAINER.get(), CentrifugeScreen::new);
        ScreenManager.registerFactory(ModContainers.MECHANICAL_CENTRIFUGE_CONTAINER.get(), MechanicalCentrifugeScreen::new);
        ScreenManager.registerFactory(ModContainers.CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), CentrifugeMultiblockScreen::new);
        ScreenManager.registerFactory(ModContainers.ELITE_CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), CentrifugeMultiblockScreen::new);
        ScreenManager.registerFactory(ModContainers.UNVALIDATED_APIARY_CONTAINER.get(), UnvalidatedApiaryScreen::new);
        ScreenManager.registerFactory(ModContainers.VALIDATED_APIARY_CONTAINER.get(), ValidatedApiaryScreen::new);
        ScreenManager.registerFactory(ModContainers.APIARY_STORAGE_CONTAINER.get(), ApiaryStorageScreen::new);
        ScreenManager.registerFactory(ModContainers.APIARY_BREEDER_CONTAINER.get(), ApiaryBreederScreen::new);
        ScreenManager.registerFactory(ModContainers.HONEY_GENERATOR_CONTAINER.get(), HoneyGeneratorScreen::new);
        ScreenManager.registerFactory(ModContainers.ENDER_BEECON_CONTAINER.get(), EnderBeeconScreen::new);
        RenderTypeLookup.setRenderLayer(ModBlocks.GOLD_FLOWER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.PREVIEW_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ERRORED_PREVIEW_BLOCK.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ENDER_BEECON.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.PURPUR_HONEY_TANK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.NETHER_HONEY_TANK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.WOODEN_HONEY_TANK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.HONEY_GENERATOR.get(), RenderType.getTranslucent());

        ItemModelPropertiesHandler.registerProperties();
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.HONEY_TANK_TILE_ENTITY.get(), RenderHoneyTank::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.HONEY_GENERATOR_ENTITY.get(), RenderHoneyGenerator::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), RenderEnderBeecon::new);
        event.enqueueWork(FluidRender::setHoneyRenderType);
    }
}
