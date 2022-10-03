package com.teamresourceful.resourcefulbees.client.event;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.client.color.ColorHandler;
import com.teamresourceful.resourcefulbees.client.gui.overlay.BeeLocatorOverlay;
import com.teamresourceful.resourcefulbees.client.gui.screen.*;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeInputScreen;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeItemOutputScreen;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeVoidScreen;
import com.teamresourceful.resourcefulbees.client.models.ModelHandler;
import com.teamresourceful.resourcefulbees.client.render.entities.CustomBeeRenderer;
import com.teamresourceful.resourcefulbees.client.render.fluids.FluidRender;
import com.teamresourceful.resourcefulbees.client.render.items.ItemModelPropertiesHandler;
import com.teamresourceful.resourcefulbees.client.render.pet.BeeRewardRender;
import com.teamresourceful.resourcefulbees.client.render.blocks.BeeHouseSelectionBox;
import com.teamresourceful.resourcefulbees.client.render.blocks.RenderEnderBeecon;
import com.teamresourceful.resourcefulbees.client.render.blocks.RenderHoneyGenerator;
import com.teamresourceful.resourcefulbees.client.render.blocks.RenderSolidificationChamber;
import com.teamresourceful.resourcefulbees.client.render.blocks.centrifuge.CentrifugeCrankRenderer;
import com.teamresourceful.resourcefulbees.client.render.blocks.centrifuge.CentrifugeRenderer;
import com.teamresourceful.resourcefulbees.client.screens.centrifuge.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.common.block.base.BeeHouseBlock;
import com.teamresourceful.resourcefulbees.common.block.base.BeeHouseTopBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public final class ClientEventHandlers {

    //TODO some methods here get called from distRunWhenOn's and could probably be merged into the FMLClientSetupEvent instead
    private ClientEventHandlers() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static boolean setupsDone = false;

    public static void clientStuff() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::onAddAdditional);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModelHandler::onModelBake);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onItemColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ColorHandler::onBlockColors);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::addLayers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::onRegisterGuiOverlay);

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::recipesLoaded);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::onTagsUpdated);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::onBlockHighlight);

        Sheets.addWoodType(ModBlocks.WAXED_WOOD_TYPE);
    }

    public static void onRegisterGuiOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("bee_locator", BeeLocatorOverlay.INSTANCE);
    }

    public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        BlockState state = event.getCamera().getEntity().level.getBlockState(event.getTarget().getBlockPos());
        if (state.getBlock() instanceof BeeHouseBlock || state.getBlock() instanceof BeeHouseTopBlock) {
            event.setCanceled(true);
            VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.lines());
            boolean shouldFlip = state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) && state.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis().equals(Direction.Axis.X);
            BeeHouseSelectionBox.renderSelectionBox(consumer, event.getPoseStack(), event.getCamera().getPosition(), event.getTarget().getBlockPos(), (state.getBlock() instanceof BeeHouseTopBlock ? 1 : 0), shouldFlip);
        }
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

            Level level = Minecraft.getInstance().level;
            RegistryAccess access = level == null ? null : level.registryAccess();
            BeeRegistry.getRegistry().regenerateCustomBeeData(access);
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
                EntityRenderers.register(entityType.get(),
                        manager -> new CustomBeeRenderer<>(manager, BeeRegistry.getRegistry().getBeeData(s).renderData())));

        registerScreens();

        ItemModelPropertiesHandler.registerProperties();
        registerTERs();
        event.enqueueWork(FluidRender::setHoneyRenderType);
    }

    private static void registerTERs() {
        BlockEntityRenderers.register(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), RenderHoneyGenerator::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY.get(), RenderSolidificationChamber::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), RenderEnderBeecon::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.WAXED_SIGN_ENTITY.get(), SignRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get(), CentrifugeRenderer::new);
        BlockEntityRenderers.register(ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get(), CentrifugeCrankRenderer::new);
    }

    private static void registerScreens() {
        MenuScreens.register(ModMenus.VALIDATED_APIARY_CONTAINER.get(), ApiaryScreen::new);
        MenuScreens.register(ModMenus.BREEDER_MENU.get(), BreederScreen::new);
        MenuScreens.register(ModMenus.HONEY_GENERATOR_CONTAINER.get(), HoneyGeneratorScreen::new);
        MenuScreens.register(ModMenus.ENDER_BEECON_CONTAINER.get(), EnderBeeconScreen::new);
        MenuScreens.register(ModMenus.SOLIDIFICATION_CHAMBER_CONTAINER.get(), SolidificationChamberScreen::new);
        MenuScreens.register(ModMenus.HONEY_POT_CONTAINER.get(), HoneyPotScreen::new);
        MenuScreens.register(ModMenus.CENTRIFUGE_MENU.get(), NormalCentrifugeScreen::new);

        //centrifuge
        MenuScreens.register(ModMenus.CENTRIFUGE_INPUT_CONTAINER.get(), CentrifugeInputScreen::new);
        MenuScreens.register(ModMenus.CENTRIFUGE_ITEM_OUTPUT_CONTAINER.get(), CentrifugeItemOutputScreen::new);
        MenuScreens.register(ModMenus.CENTRIFUGE_VOID_CONTAINER.get(), CentrifugeVoidScreen::new);
        MenuScreens.register(ModMenus.CENTRIFUGE_TERMINAL_CONTAINER.get(), CentrifugeTerminalScreen::new);
    }
}
