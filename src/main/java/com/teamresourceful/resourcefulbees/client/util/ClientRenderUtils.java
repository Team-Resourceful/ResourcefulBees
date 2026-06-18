package com.teamresourceful.resourcefulbees.client.util;

import com.teamresourceful.resourcefullib.common.caches.CacheableBiFunction;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class ClientRenderUtils {

    private ClientRenderUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    public static final CacheableBiFunction<Identifier, Identifier, Identifier> DEFAULT_TEXTURER = new CacheableBiFunction<>((texture, other) -> texture == other ? texture : Minecraft.getInstance().getResourceManager().getResource(texture).isPresent() ? texture : other);

   /* public static void renderEntity(GuiGraphics graphics, Entity entity, float x, float y, float rotation, float renderScale) {
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
                entityRenderer.render(entity, 0, 0, 0.0D, mc.getTimer().getGameTimeDeltaPartialTick(true), 1, pose, buffer, LightTexture.FULL_BRIGHT);
                buffer.endBatch();
            }
        }
    }*/

    /*todo public static void drawFluid(GuiGraphics graphics, int height, int width, FluidHolder holder, int x, int y) {
        if (holder.isEmpty()) return;
        drawFluid(graphics, height, width, ClientFluidHooks.getFluidSprite(holder), ClientFluidHooks.getFluidColor(holder), x, y);
    }*//*

    public static void drawFluid(GuiGraphics graphics, int height, int width, TextureAtlasSprite sprite, int color, int x, int y) {
        int remainder = height % 16;
        int splits = (height - remainder) / 16;
        if (remainder != 0) splits++;

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        RenderSystem.setShaderColor(((color >> 16) & 0xFF)/ 255.0F, ((color >> 8) & 0xFF)/ 255.0F, (color & 0xFF)/ 255.0F,  ((color >> 24) & 0xFF)/ 255.0F);
        for (int i = 0; i < splits; i++) {
            int splitHeight = (i + 1 == splits && remainder != 0 ? remainder : 16);
            graphics.blit(x, y + height - (splitHeight + (i * 16)), 0, width, splitHeight, sprite);
        }

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }*/

}
