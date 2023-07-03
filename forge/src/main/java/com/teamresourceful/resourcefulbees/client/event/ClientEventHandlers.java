package com.teamresourceful.resourcefulbees.client.event;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.client.ResourcefulBeesClient;
import com.teamresourceful.resourcefulbees.client.overlay.BeeLocatorOverlay;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.common.registries.custom.BeeRegistry;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.platform.client.events.*;
import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import com.teamresourceful.resourcefulconfig.common.config.ResourcefulConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


public final class ClientEventHandlers {

    //TODO some methods here get called from distRunWhenOn's and could probably be merged into the FMLClientSetupEvent instead
    private ClientEventHandlers() {
        throw new UtilityClassError();
    }

    private static boolean setupsDone = false;

    public static void clientStuff() {
        ResourcefulBeesClient.init();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> {
                    ResourcefulConfig config = ResourcefulBees.CONFIGURATOR.getConfig(GeneralConfig.class);
                    if (config == null) {
                        return null;
                    }
                    return new ConfigScreen(null, config);
                })
        );

        var eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        FMLJavaModLoadingContext.get().getModEventBus().addListener((ModelEvent.RegisterAdditional event) ->
            RegisterAdditionaModelsEvent.EVENT.fire(new RegisterAdditionaModelsEvent(event::register))
        );
        FMLJavaModLoadingContext.get().getModEventBus().addListener((ModelEvent.BakingCompleted event) ->
            ModelBakingCompletedEvent.EVENT.fire(new ModelBakingCompletedEvent(event.getModelBakery()))
        );
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::clientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener((EntityRenderersEvent.AddLayers event) ->
            RegisterEntityLayersEvent.EVENT.fire(new RegisterEntityLayersEvent(event::getSkin))
        );
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandlers::onRegisterGuiOverlay);

        eventBus.addListener((RegisterColorHandlersEvent.Item event) ->
            RegisterColorHandlerEvent.EVENT.fire(new RegisterColorHandlerEvent(event.getItemColors(), event.getBlockColors(), RegisterColorHandlerEvent.Phase.ITEMS))
        );
        eventBus.addListener((RegisterColorHandlersEvent.Block event) ->
            RegisterColorHandlerEvent.EVENT.fire(new RegisterColorHandlerEvent(null, event.getBlockColors(), RegisterColorHandlerEvent.Phase.BLOCKS))
        );
        eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
            RegisterRendererEvent.EVENT.fire(new RegisterRendererEvent(event::registerEntityRenderer, event::registerBlockEntityRenderer))
        );

        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::recipesLoaded);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ClientEventHandlers::onTagsUpdated);
        MinecraftForge.EVENT_BUS.addListener((ScreenEvent.Opening event) -> {
            ScreenOpenEvent screenOpenEvent = new ScreenOpenEvent(event.getScreen());
            ScreenOpenEvent.EVENT.fire(screenOpenEvent);
            if (event.getScreen() != screenOpenEvent.getScreen()) {
                event.setNewScreen(screenOpenEvent.getScreen());
                event.setCanceled(true);
            }
        });
    }

    public static void onRegisterGuiOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("bee_locator", (gui, stack, partTicks, x, y) -> BeeLocatorOverlay.INSTANCE.render(gui.getMinecraft(), stack, partTicks, x, y));
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

    private static void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            RegisterScreensEvent.EVENT.fire(new RegisterScreensEvent(new RegisterScreensEvent.ScreenRegistrar() {
                @Override
                public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, RegisterScreensEvent.ScreenFactory<M, U> factory) {
                    MenuScreens.register(type, factory::create);
                }
            }));
            RegisterItemPropertiesEvent.EVENT.fire(new RegisterItemPropertiesEvent(ItemProperties::register));
            RegisterRenderLayersEvent.EVENT.fire(new RegisterRenderLayersEvent(
                    ItemBlockRenderTypes::setRenderLayer,
                    ItemBlockRenderTypes::setRenderLayer
            ));
        });
        event.enqueueWork(() -> Sheets.addWoodType(ModBlocks.WAXED_WOOD_TYPE));
    }
}
