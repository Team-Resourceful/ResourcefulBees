package com.teamresourceful.resourcefulbees.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.common.caches.CacheableBiFunction;
import net.minecraft.client.Minecraft;
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

    public static void renderEntity(PoseStack stack, Entity entity, float x, float y, float rotation, float renderScale) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) entity.tickCount = mc.player.tickCount;
        float scaledSize = 15 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        if (mc.player != null) {
            try (var ignored = new CloseablePoseStack(stack)) {
                stack.translate(10, 15 * renderScale, 0.5);
                stack.translate(x, y, 1);
                stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                stack.translate(0, 0, 100);
                stack.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
                stack.mulPose(Vector3f.YP.rotationDegrees(rotation));
                EntityRenderDispatcher entityRenderer = mc.getEntityRenderDispatcher();
                MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
                entityRenderer.render(entity, 0, 0, 0.0D, mc.getFrameTime(), 1, stack, buffer, LightTexture.FULL_BRIGHT);
                buffer.endBatch();
            }
        }
    }

}
