package com.teamresourceful.resourcefulbees.client;

import com.geckolib.renderer.GeoBlockRenderer;
import com.teamresourceful.resourcefulbees.api.registry.BeeRegistry;
import com.teamresourceful.resourcefulbees.client.fluids.ModClientFluidProperties;
import com.teamresourceful.resourcefulbees.client.model.property.FilledBeeJarProperty;
import com.teamresourceful.resourcefulbees.client.overlay.BeeLocatorOverlay;
import com.teamresourceful.resourcefulbees.client.recipe.RBeesClientRecipes;
import com.teamresourceful.resourcefulbees.client.rendering.blocks.EnderBeeconRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.blocks.HoneyGenRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.blocks.SolidificationChamberRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.blocks.centrifuge.CentrifugeCrankRenderer;
import com.teamresourceful.resourcefulbees.client.rendering.entities.CustomBeeRenderer;
import com.teamresourceful.resourcefulbees.client.screen.*;
import com.teamresourceful.resourcefulbees.client.tints.*;
import com.teamresourceful.resourcefulbees.common.blocks.CustomHoneyBlock;
import com.teamresourceful.resourcefulbees.common.blocks.HoneycombBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModIdentifier;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEntities;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModMenuTypes;
import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.ArrayList;
import java.util.List;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = ModConstants.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT)
public class ResourcefulBeesClient {

    private static final Identifier BEE_LOCATOR_OVERLAY = ModIdentifier.of("bee_locator");

    public ResourcefulBeesClient(ModContainer container) {
        ModClientFluidProperties.registerHoneyFluids();
        NeoForge.EVENT_BUS.addListener(MissingRegistryScreen::onScreenOpening);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // TODO document why this method is empty
    }

    @SubscribeEvent // on the mod event bus only on the physical client
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ModEntities.getModBees().forEach((s, entityType) ->
                event.registerEntityRenderer(entityType.get(), context -> new CustomBeeRenderer<>(context, BeeRegistry.get().getBeeData(s).getRenderData()))
        );

        event.registerBlockEntityRenderer(ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get(), context -> new GeoBlockRenderer<>(context, ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get()));
        event.registerBlockEntityRenderer(ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get(), context -> new CentrifugeCrankRenderer<>(context, ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get()));
        event.registerBlockEntityRenderer(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), EnderBeeconRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY.get(), SolidificationChamberRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), HoneyGenRenderer::new);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.APIARY.get(), ApiaryScreen::new);
        event.register(ModMenuTypes.CENTRIFUGE.get(), CentrifugeScreen::new);
        event.register(ModMenuTypes.BREEDER.get(), BreederScreen::new);
        event.register(ModMenuTypes.SOLIDIFICATION_CHAMBER.get(), SolidificationChamberScreen::new);
        event.register(ModMenuTypes.ENDER_BEECON.get(), EnderBeeconScreen::new);
        event.register(ModMenuTypes.HONEY_GENERATOR.get(), HoneyGeneratorScreen::new);
        event.register(ModMenuTypes.HONEY_POT.get(), HoneyPotScreen::new);
    }

    @SubscribeEvent
    public static void itemColor(RegisterColorHandlersEvent.ItemTintSources event){
        event.register(ModIdentifier.of("honeycomb"), HoneycombTintSource.CODEC);
        event.register(ModIdentifier.of("honeycomb_block"), HoneycombBlockItemTintSource.CODEC);
        event.register(ModIdentifier.of("honey_block"), HoneyBlockBlockItemTintSource.CODEC);
        event.register(ModIdentifier.of("honey_bottle"), HoneyBottleTintSource.CODEC);
        event.register(ModIdentifier.of("filled_bee_jar"), FilledBeeJarTintSource.CODEC);
        event.register(ModIdentifier.of("bee_spawn_egg"), BeeSpawnEggTintSource.CODEC);
    }

    @SubscribeEvent
    public static void blockColor(RegisterColorHandlersEvent.BlockTintSources event) {
        List<HoneycombBlock> blocks = new ArrayList<>();
        ModBlocks.HONEYCOMB_BLOCKS.stream()
                .map(RegistryEntry::get)
                .filter(HoneycombBlock.class::isInstance)
                .map(HoneycombBlock.class::cast)
                .forEach(blocks::add);
        event.register(List.of(new HoneycombBlockTintSource()), blocks.toArray(new HoneycombBlock[0]));

        List<CustomHoneyBlock> honeyBlocks = new ArrayList<>();
        ModBlocks.HONEY_BLOCKS.stream()
                .map(RegistryEntry::get)
                .filter(CustomHoneyBlock.class::isInstance)
                .map(CustomHoneyBlock.class::cast)
                .forEach(honeyBlocks::add);
        event.register(List.of(new HoneyBlockTintSource()), honeyBlocks.toArray(new CustomHoneyBlock[0]));
    }

    @SubscribeEvent
    public static void registerConditionalProperties(RegisterConditionalItemModelPropertyEvent event) {
        event.register(ModIdentifier.of("filled_bee_jar"), FilledBeeJarProperty.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, BEE_LOCATOR_OVERLAY, BeeLocatorOverlay.INSTANCE::render);
    }

    @SubscribeEvent
    public static void recipesReceived(RecipesReceivedEvent event) {
        RBeesClientRecipes.update(event.getRecipeMap());
    }

    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
        RBeesClientRecipes.clear();
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level) {
            com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry.getRegistry().regenerateCustomBeeData(level.registryAccess());
        }
    }
}
