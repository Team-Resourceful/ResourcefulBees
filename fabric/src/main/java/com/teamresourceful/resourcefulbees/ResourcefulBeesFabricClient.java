package com.teamresourceful.resourcefulbees;

import com.teamresourceful.resourcefulbees.client.ResourcefulBeesClient;
import com.teamresourceful.resourcefulbees.platform.client.events.*;
import com.teamresourceful.resourcefulbees.platform.client.events.lifecycle.ClientLoadingCompletedEvent;
import com.teamresourceful.resourcefulbees.platform.client.renderer.overlay.OverlayRenderer;
import com.teamresourceful.resourcefulbees.platform.common.events.lifecycle.LoadingCompletedEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResourcefulBeesFabricClient implements ClientModInitializer {

    private static Map<String, OverlayRenderer> OVERLAY_RENDERERS = new LinkedHashMap<>();

    @Override
    public void onInitializeClient() {
        ResourcefulBeesClient.init();

        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) ->
            RegisterAdditionaModelsEvent.EVENT.fire(new RegisterAdditionaModelsEvent(out))
        );
        RegisterOverlayEvent.EVENT.fire(new RegisterOverlayEvent(OVERLAY_RENDERERS::put));
        HudRenderCallback.EVENT.register((graphics, partialTicks) -> {
            for (OverlayRenderer renderer : OVERLAY_RENDERERS.values()) {
                renderer.render(Minecraft.getInstance(), graphics, partialTicks, Minecraft.getInstance().getWindow().getGuiScaledWidth(), Minecraft.getInstance().getWindow().getGuiScaledHeight());
            }
        });
        RegisterColorHandlerEvent.EVENT.fire(new RegisterColorHandlerEvent(ColorProviderRegistry.ITEM::register, ColorProviderRegistry.BLOCK::register));
        RegisterRendererEvent.EVENT.fire(new RegisterRendererEvent(EntityRendererRegistry::register, BlockEntityRenderers::register));
        RegisterScreensEvent.EVENT.fire(new RegisterScreensEvent(new RegisterScreensEvent.ScreenRegistrar() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type, RegisterScreensEvent.ScreenFactory<M, U> factory) {
                MenuScreens.register(type, factory::create);
            }
        }));
        RegisterItemPropertiesEvent.EVENT.fire(new RegisterItemPropertiesEvent(ItemProperties::register));
        RegisterRenderLayersEvent.EVENT.fire(new RegisterRenderLayersEvent(
                BlockRenderLayerMap.INSTANCE::putBlock,
                BlockRenderLayerMap.INSTANCE::putFluid
        ));

        ClientLoadingCompletedEvent.fire();
        LoadingCompletedEvent.fire();
    }
}
