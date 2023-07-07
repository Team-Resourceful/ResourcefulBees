package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.client.ResourcefulBeesClient;
import com.teamresourceful.resourcefulbees.common.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.config.GeneralConfig;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefulbees.platform.client.events.*;
import com.teamresourceful.resourcefulconfig.client.ConfigScreen;
import net.minecraft.Optionull;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


public final class ResourcefulBeesForgeClient {

    private ResourcefulBeesForgeClient() {
        throw new UtilityClassError();
    }

    public static void init() {
        ResourcefulBeesClient.init();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) ->
                        Optionull.map(ResourcefulBees.CONFIGURATOR.getConfig(GeneralConfig.class),
                                config -> new ConfigScreen(parent, null, config)))
        );

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener((ModelEvent.RegisterAdditional event) ->
            RegisterAdditionaModelsEvent.EVENT.fire(new RegisterAdditionaModelsEvent(event::register))
        );
        eventBus.addListener((ModelEvent.BakingCompleted event) ->
            ModelBakingCompletedEvent.EVENT.fire(new ModelBakingCompletedEvent(event.getModelBakery()))
        );
        eventBus.addListener((ModelEvent.ModifyBakingResult event) ->
                ModelModifyResultEvent.EVENT.fire(new ModelModifyResultEvent(event.getModels(), event.getModelBakery()))
        );
        eventBus.addListener((EntityRenderersEvent.AddLayers event) ->
            RegisterEntityLayersEvent.EVENT.fire(new RegisterEntityLayersEvent(event::getSkin))
        );
        eventBus.addListener((RegisterGuiOverlaysEvent event) ->
            RegisterOverlayEvent.EVENT.fire(new RegisterOverlayEvent(
                    (id, overlay) -> event.registerAboveAll(id,
                            (gui, stack, partTicks, x, y) -> overlay.render(gui.getMinecraft(), stack, partTicks, x, y)
                    )
            ))
        );
        eventBus.addListener((RegisterColorHandlersEvent.Item event) ->
            RegisterColorHandlerEvent.EVENT.fire(new RegisterColorHandlerEvent(event::register, null))
        );
        eventBus.addListener((RegisterColorHandlersEvent.Block event) ->
            RegisterColorHandlerEvent.EVENT.fire(new RegisterColorHandlerEvent(null, event::register))
        );
        eventBus.addListener((EntityRenderersEvent.RegisterRenderers event) ->
            RegisterRendererEvent.EVENT.fire(new RegisterRendererEvent(event::registerEntityRenderer, event::registerBlockEntityRenderer))
        );
        eventBus.addListener(ResourcefulBeesForgeClient::clientSetup);

        MinecraftForge.EVENT_BUS.addListener((ScreenEvent.Opening event) -> {
            ScreenOpenEvent screenOpenEvent = new ScreenOpenEvent(event.getScreen());
            ScreenOpenEvent.EVENT.fire(screenOpenEvent);
            if (event.getScreen() != screenOpenEvent.getScreen()) {
                event.setNewScreen(screenOpenEvent.getScreen());
                event.setCanceled(true);
            }
        });
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
    }
}
