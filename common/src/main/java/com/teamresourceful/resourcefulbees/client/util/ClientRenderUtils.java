package com.teamresourceful.resourcefulbees.client.util;

import com.mojang.math.Axis;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.common.caches.CacheableBiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public final class ClientRenderUtils {

    private ClientRenderUtils() {
        throw new UtilityClassError();
    }

    public static final CacheableBiFunction<ResourceLocation, ResourceLocation, ResourceLocation> DEFAULT_TEXTURER = new CacheableBiFunction<>((texture, other) -> texture == other ? texture : Minecraft.getInstance().getResourceManager().getResource(texture).isPresent() ? texture : other);

    public static void renderEntity(GuiGraphics graphics, Entity entity, float x, float y, float rotation, float renderScale) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) entity.tickCount = mc.player.tickCount;
        float scaledSize = 15 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        if (mc.player != null) {
            try (var pose = new CloseablePoseStack(graphics)) {
                pose.translate(10, 15 * renderScale, 0.5);
                pose.translate(x, y, 1);
                pose.mulPose(Axis.ZP.rotationDegrees(180.0F));
                pose.translate(0, 0, 100);
                pose.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
                pose.mulPose(Axis.YP.rotationDegrees(rotation));
                EntityRenderDispatcher entityRenderer = mc.getEntityRenderDispatcher();
                MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
                entityRenderer.render(entity, 0, 0, 0.0D, mc.getFrameTime(), 1, pose, buffer, LightTexture.FULL_BRIGHT);
                buffer.endBatch();
            }
        }
    }

}
