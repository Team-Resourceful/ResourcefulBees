package com.teamresourceful.resourcefulbees.client;

import com.teamresourceful.resourcefulbees.client.screen.MissingRegistryScreen;
import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModBlockEntityTypes;
import com.teamresourceful.resourcefulbees.platform.client.events.RegisterRendererEvent;
import com.teamresourceful.resourcefulbees.platform.client.events.ScreenOpenEvent;
import com.teamresourceful.resourcefullib.common.color.Color;
import net.minecraft.client.renderer.blockentity.SignRenderer;

public class ResourcefulBeesClient {

    public static void init() {
        ScreenOpenEvent.EVENT.addListener(MissingRegistryScreen::onScreenChange);
        RegisterRendererEvent.EVENT.addListener(ResourcefulBeesClient::registerRenderers);

        Color.initRainbow();
    }

    public static void registerRenderers(RegisterRendererEvent event) {
        event.register(ModBlockEntityTypes.WAXED_SIGN_ENTITY.get(), SignRenderer::new);
    }
}
