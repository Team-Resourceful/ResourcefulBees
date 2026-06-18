package com.teamresourceful.resourcefulbees.client;

import com.teamresourceful.resourcefulbees.client.data.ModelHandler;
import com.teamresourceful.resourcefulbees.client.screen.MissingRegistryScreen;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModPaths;
import com.teamresourceful.resourcefulbees.mixin.client.PackRepositoryAccessor;
import com.teamresourceful.resourcefulbees.client.events.ModelBakingCompletedEvent;
import com.teamresourceful.resourcefulbees.client.events.ModelModifyResultEvent;
import com.teamresourceful.resourcefulbees.client.events.RegisterAdditionalModelsEvent;
import com.teamresourceful.resourcefulbees.client.events.ScreenOpenEvent;
import com.teamresourceful.resourcefulbees.events.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

import java.util.Optional;

public class ResourcefulBeesClient {

    public static void init() {
        ScreenOpenEvent.EVENT.addListener(MissingRegistryScreen::onScreenChange);
/*        RegisterRendererEvent.EVENT.addListener(ResourcefulBeesClient::registerRenderers);
        RegisterColorHandlerEvent.EVENT.addListener(ResourcefulBeesClient::registerColors);
        RegisterScreensEvent.EVENT.addListener(ResourcefulBeesClient::registerScreens);
        RegisterItemPropertiesEvent.EVENT.addListener(ResourcefulBeesClient::registerItemProperties);
        RegisterRenderLayersEvent.EVENT.addListener(ResourcefulBeesClient::registerRenderLayers);
        RegisterEntityLayersEvent.EVENT.addListener(ResourcefulBeesClient::registerEntityLayers);
        RegisterOverlayEvent.EVENT.addListener(ResourcefulBeesClient::registerOverlay);*/
        RegisterAdditionalModelsEvent.EVENT.addListener(ModelHandler::onAddAdditional);
        ModelBakingCompletedEvent.EVENT.addListener(ModelHandler::onModelBake);
        ModelModifyResultEvent.EVENT.addListener(ModelHandler::onModifyModel);
        UpdateEvent.EVENT.addListener(ClientDataSetup::onUpdates);

        //Color.initRainbow();

        loadResources();
    }

/*    public static void registerOverlay(RegisterOverlayEvent event) {
        event.register("bee_locator", BeeLocatorOverlay.INSTANCE);
    }

    public static void registerRenderLayers(RegisterRenderLayersEvent event) {
        event.registerFluids(ModFluids.STILL_HONEY_FLUIDS.boundStream(), RenderType.translucent());
        event.registerFluids(ModFluids.FLOWING_HONEY_FLUIDS.boundStream(), RenderType.translucent());

        event.registerBlock(ModBlocks.HONEY_GLASS.get(), RenderType.translucent());
        event.registerBlock(ModBlocks.HONEY_GLASS_PLAYER.get(), RenderType.translucent());
        event.registerBlock(ModBlocks.SOLIDIFICATION_CHAMBER.get(), RenderType.translucent());
        event.registerBlock(ModBlocks.HONEY_GENERATOR.get(), RenderType.translucent());
        event.registerBlock(ModBlocks.FAKE_FLOWER.get(), RenderType.cutout());
        event.registerBlock(ModBlocks.GOLD_FLOWER.get(), RenderType.cutout());
        event.registerBlock(ModBlocks.ENDER_BEECON.get(), RenderType.cutout());
        event.registerBlock(ModBlocks.WAXED_DOOR.get(), RenderType.cutout());
        event.registerBlock(ModBlocks.WAXED_TRAPDOOR.get(), RenderType.cutout());

        event.registerBlocks(ModBlocks.HONEY_BLOCKS.boundStream(), RenderType.translucent());

        event.registerBlocks(ModBlocks.HIVES.boundStream(), RenderType.cutout());
    }

    public static void registerItemProperties(RegisterItemPropertiesEvent event) {
        event.register(ModItems.BEE_JAR.get(), ResourceLocation.tryParse("filled"), (stack, level, entity, seed) -> BeeJarItem.isFilled(stack) ? 1.0F : 0.0F);
        event.register(ModItems.BEEPEDIA.get(), ResourceLocation.tryParse("creative"), (stack, level, entity, seed) -> BeepediaItem.isCreative(stack) ? 1.0F : 0.0F);
    }

    public static void registerScreens(RegisterScreensEvent event) {
        event.register(ModMenuTypes.APIARY.get(), ApiaryScreen::new);
        event.register(ModMenuTypes.BREEDER.get(), BreederScreen::new);
        event.register(ModMenuTypes.HONEY_GENERATOR.get(), HoneyGeneratorScreen::new);
        event.register(ModMenuTypes.ENDER_BEECON.get(), EnderBeeconScreen::new);
        event.register(ModMenuTypes.SOLIDIFICATION_CHAMBER_CONTAINER.get(), SolidificationChamberScreen::new);
        event.register(ModMenuTypes.FAKE_FLOWER.get(), FakeFlowerScreen::new);
        event.register(ModMenuTypes.HONEY_POT.get(), HoneyPotScreen::new);
        event.register(ModMenuTypes.CENTRIFUGE.get(), NormalCentrifugeScreen::new);
    }

    @SuppressWarnings("unchecked")
    public static void registerEntityLayers(RegisterEntityLayersEvent event) {
        PlayerRenderer defaultRenderer = event.getSkin("default");
        PlayerRenderer slimRenderer = event.getSkin("slim");
        LivingEntityRendererInvoker<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> defaultInvoker = ((LivingEntityRendererInvoker<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) defaultRenderer);
        LivingEntityRendererInvoker<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> slimInvoker = ((LivingEntityRendererInvoker<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>) slimRenderer);
        if (defaultRenderer != null && slimRenderer != null) {
            defaultInvoker.invokeAddLayer(new BeeRewardRender(defaultRenderer));
            slimInvoker.invokeAddLayer(new BeeRewardRender(slimRenderer));
        }
    }

    public static void registerRenderers(RegisterRendererEvent event) {
        event.register(ModBlockEntityTypes.WAXED_SIGN_ENTITY.get(), SignRenderer::new);
        event.register(ModBlockEntityTypes.WAXED_HANGING_SIGN_ENTITY.get(), HangingSignRenderer::new);
        event.register(ModBlockEntityTypes.HONEY_GENERATOR_ENTITY.get(), RenderHoneyGenerator::new);
        event.register(ModBlockEntityTypes.SOLIDIFICATION_CHAMBER_TILE_ENTITY.get(), RenderSolidificationChamber::new);
        event.register(ModBlockEntityTypes.ENDER_BEECON_TILE_ENTITY.get(), EnderBeeconRenderer::new);
        event.register(ModBlockEntityTypes.BASIC_CENTRIFUGE_ENTITY.get(), CentrifugeRenderer::new);
        event.register(ModBlockEntityTypes.CENTRIFUGE_CRANK_ENTITY.get(), CentrifugeCrankRenderer::new);

        ModEntities.getModBees().forEach((s, entityType) ->
                event.register(entityType.get(), manager -> new CustomBeeRenderer<>(manager, BeeRegistry.get().getBeeData(s).getRenderData()))
        );
        event.register(ModEntities.THROWN_MUTATED_POLLEN.get(), ThrownItemRenderer::new);
    }

    public static void registerColors(RegisterColorHandlerEvent event) {
        if (event.items() != null) {
            Item[] collect = Stream.concat(
                    Stream.concat(
                            Stream.concat(
                                    ModItems.HONEYCOMB_ITEMS.boundStream(),
                                    ModItems.HONEYCOMB_BLOCK_ITEMS.boundStream()
                            ),
                            Stream.concat(
                                    ModItems.HONEY_BUCKET_ITEMS.boundStream(),
                                    ModItems.HONEY_BOTTLE_ITEMS.boundStream()
                            )
                    ),
                    Stream.concat(
                            ModItems.HONEY_BLOCK_ITEMS.boundStream(),
                            ModItems.SPAWN_EGG_ITEMS.boundStream()
                    )
            ).toArray(Item[]::new);
            event.register(ColoredObject::getItemColor, collect);
            event.register(BeeJarItem::getColor, ModItems.BEE_JAR.get());
            event.register(MutatedPollenItem::getColor, ModItems.MUTATED_POLLEN.get());
        }
        if (event.blocks() != null) {
            event.register(ColoredObject::getBlockColor, ModBlocks.HONEYCOMB_BLOCKS.boundStream().toArray(Block[]::new));
            event.register(ColoredObject::getBlockColor, ModBlocks.HONEY_BLOCKS.boundStream().toArray(Block[]::new));
        }
    }*/

    private static void loadResources() {
        //This is needed for data gen as Minecraft.getInstance() is null in data gen.
        //noinspection ConstantConditions
        if (Minecraft.getInstance() == null) return;

        PackRepositoryAccessor accessor = (PackRepositoryAccessor) Minecraft.getInstance().getResourcePackRepository();

        accessor.getSources().add(consumer -> {
            final PackLocationInfo locationInfo = new PackLocationInfo(ModConstants.MOD_ID, Component.literal(ModConstants.MOD_ID), PackSource.BUILT_IN, Optional.empty());
            final PackSelectionConfig selectionConfig = new PackSelectionConfig(true, Pack.Position.TOP, false);
            final Pack pack = Pack.readMetaAndCreate(
                    locationInfo,
                    new PathPackResources.PathResourcesSupplier(ModPaths.RESOURCES),
                    PackType.CLIENT_RESOURCES,
                    selectionConfig
            );

            if (pack == null) {
                ModConstants.LOGGER.error("Failed to load resource pack, some things may not work.");
                return;
            }
            consumer.accept(pack);
        });
    }
}
