package com.resourcefulbees.resourcefulbees.init;

import com.resourcefulbees.resourcefulbees.api.beedata.CustomBeeData;
import com.resourcefulbees.resourcefulbees.client.gui.screen.*;
import com.resourcefulbees.resourcefulbees.client.models.ModelHandler;
import com.resourcefulbees.resourcefulbees.client.render.entity.GeckoBeeRenderer;
import com.resourcefulbees.resourcefulbees.client.render.fluid.FluidRender;
import com.resourcefulbees.resourcefulbees.client.render.items.ItemModelPropertiesHandler;
import com.resourcefulbees.resourcefulbees.client.render.patreon.BeeRewardRender;
import com.resourcefulbees.resourcefulbees.client.render.patreon.PetLoader;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderEnderBeecon;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderHoneyCongealer;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderHoneyGenerator;
import com.resourcefulbees.resourcefulbees.client.render.tileentity.RenderHoneyTank;
import com.resourcefulbees.resourcefulbees.lib.ModConstants;
import com.resourcefulbees.resourcefulbees.registry.*;
import com.resourcefulbees.resourcefulbees.utils.PreviewHandler;
import com.resourcefulbees.resourcefulbees.utils.color.ColorHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.ArrayList;
import java.util.List;

public class ClientEventHandlers {

    private ClientEventHandlers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static boolean setupsDone = false;

    private static boolean firstLoad = true;

    public static void clientStuff() {
        MinecraftForge.EVENT_BUS.addListener(PreviewHandler::onWorldRenderLast);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::registerModels);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::onModelBake);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onBlockColors);
        MinecraftForge.EVENT_BUS.addListener(ClientEventHandlers::onFirstLoad);
        MinecraftForge.EVENT_BUS.addListener(FluidRender::honeyOverlay);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::recipesLoaded);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::onTagsUpdated);

        Atlases.addWoodType(ModBlocks.WAXED_WOOD_TYPE);

    }

    public static void onFirstLoad(TickEvent.ClientTickEvent event) {
        if (!event.phase.equals(TickEvent.Phase.START)) return;
        Minecraft instance = Minecraft.getInstance();
        ClientPlayerEntity player = instance.player;
        if (firstLoad && !instance.isPaused() && player != null && instance.level != null && PetLoader.specialMessage != null) {
            firstLoad = false;
            List<ITextComponent> components = new ArrayList<>();
            for (String s : PetLoader.specialMessage.split("\n")) {
                components.add(new StringTextComponent(String.format(s, player.getGameProfile().getName())));
            }
            components.forEach(msg -> player.displayClientMessage(msg, false));
        }
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
            MutationSetup.setupMutations();
            FlowerSetup.setupFlowers();
            BreedingSetup.setupFeedItems();
        }
    }

    public static void registerPatreonRender() {
        EntityRendererManager manager =  Minecraft.getInstance().getEntityRenderDispatcher();
        manager.getSkinMap().get("default").addLayer(new BeeRewardRender(manager.defaultPlayerRenderer));
        manager.getSkinMap().get("slim").addLayer(new BeeRewardRender(manager.defaultPlayerRenderer));
    }

    private static void doClientStuff(final FMLClientSetupEvent event) {
        ModEntities.getModBees().forEach((s, customBee) -> RenderingRegistry.registerEntityRenderingHandler(customBee.get(), manager -> {
            CustomBeeData data = BeeRegistry.getRegistry().getBeeData(s);
            return new GeckoBeeRenderer<>(manager, data);
        }));
        ScreenManager.register(ModContainers.CENTRIFUGE_CONTAINER.get(), CentrifugeScreen::new);
        ScreenManager.register(ModContainers.MECHANICAL_CENTRIFUGE_CONTAINER.get(), MechanicalCentrifugeScreen::new);
        ScreenManager.register(ModContainers.CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), CentrifugeMultiblockScreen::new);
        ScreenManager.register(ModContainers.ELITE_CENTRIFUGE_MULTIBLOCK_CONTAINER.get(), CentrifugeMultiblockScreen::new);
        ScreenManager.register(ModContainers.UNVALIDATED_APIARY_CONTAINER.get(), UnvalidatedApiaryScreen::new);
        ScreenManager.register(ModContainers.VALIDATED_APIARY_CONTAINER.get(), ValidatedApiaryScreen::new);
        ScreenManager.register(ModContainers.APIARY_STORAGE_CONTAINER.get(), ApiaryStorageScreen::new);
        ScreenManager.register(ModContainers.APIARY_BREEDER_CONTAINER.get(), ApiaryBreederScreen::new);
        ScreenManager.register(ModContainers.HONEY_GENERATOR_CONTAINER.get(), HoneyGeneratorScreen::new);
        ScreenManager.register(ModContainers.ENDER_BEECON_CONTAINER.get(), EnderBeeconScreen::new);
        ScreenManager.register(ModContainers.HONEY_CONGEALER_CONTAINER.get(), HoneyCongealerScreen::new);
        ScreenManager.register(ModContainers.HONEY_TANK_CONTAINER.get(), HoneyTankScreen::new);
        RenderTypeLookup.setRenderLayer(ModBlocks.GOLD_FLOWER.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.PREVIEW_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ERRORED_PREVIEW_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.ENDER_BEECON.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.HONEY_CONGEALER.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.PURPUR_HONEY_TANK.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.NETHER_HONEY_TANK.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.WOODEN_HONEY_TANK.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.HONEY_GENERATOR.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.HONEY_GLASS.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.HONEY_GLASS_PLAYER.get(), RenderType.translucent());

        // bee nests need transparency for overlay
        RenderTypeLookup.setRenderLayer(ModBlocks.OAK_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.SPRUCE_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.BIRCH_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.BROWN_MUSHROOM_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.CRIMSON_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.DARK_OAK_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.RED_MUSHROOM_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.WARPED_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.WARPED_NYLIUM_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.ACACIA_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.GRASS_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.JUNGLE_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.NETHER_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.PRISMARINE_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.PURPUR_BEE_NEST.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.WITHER_BEE_NEST.get(), RenderType.translucent());

        RenderTypeLookup.setRenderLayer(ModBlocks.WAXED_DOOR.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(ModBlocks.WAXED_TRAPDOOR.get(), RenderType.cutout());

        RenderTypeLookup.setRenderLayer(ModBlocks.T1_BEEHIVE.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.T2_BEEHIVE.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.T3_BEEHIVE.get(), RenderType.translucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.T4_BEEHIVE.get(), RenderType.translucent());

        ItemModelPropertiesHandler.registerProperties();
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.HONEY_TANK_TILE_ENTITY.get(), RenderHoneyTank::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.HONEY_GENERATOR_ENTITY.get(), RenderHoneyGenerator::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.HONEY_CONGEALER_TILE_ENTITY.get(), RenderHoneyCongealer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), RenderEnderBeecon::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.WAXED_SIGN_TILE_ENTITY.get(), SignTileEntityRenderer::new);
        event.enqueueWork(FluidRender::setHoneyRenderType);
    }
}
